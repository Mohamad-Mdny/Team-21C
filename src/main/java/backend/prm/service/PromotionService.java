package backend.prm.service;

import backend.models.Item;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
import backend.prm.report.CampaignHitReportRow;
import backend.prm.report.CampaignReportRow;
import backend.prm.report.SalesReportRow;
import backend.prm.repository.ProductDAO;
import backend.prm.repository.PromotionRepository;

import java.time.LocalDateTime;
import java.util.List;

public class PromotionService {

    private final PromotionRepository repository;
    private final ProductDAO productDAO = new ProductDAO();

    public PromotionService(PromotionRepository repository) {
        this.repository = repository;
    }

    public PromotionCampaign createCampaign(String title,
                                            String description,
                                            LocalDateTime startDateTime,
                                            LocalDateTime endDateTime,
                                            double discountPercent) {
        validateCampaignInput(title, startDateTime, endDateTime, discountPercent);

        PromotionCampaign campaign = new PromotionCampaign(
                0,
                null,
                title.trim(),
                description,
                startDateTime,
                endDateTime,
                discountPercent
        );

        return repository.saveCampaign(campaign);
    }

    public List<PromotionCampaign> getAllCampaigns() {
        return repository.findAllCampaigns();
    }

    public List<PromotionCampaign> getActiveCampaigns(LocalDateTime now) {
        return repository.findActiveCampaigns(now);
    }

    public PromotionCampaign getCampaignById(long campaignId) {
        return getCampaignOrThrow(campaignId);
    }

    public void cancelCampaign(long campaignId) {
        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        if (campaign.getStatus(LocalDateTime.now()) == PromotionStatus.EXPIRED) {
            throw new IllegalStateException("Expired campaigns cannot be cancelled.");
        }
        campaign.setCancelledAt(LocalDateTime.now());
        repository.updateCampaign(campaign);
    }

    public void reactivateCampaign(long campaignId) {
        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        if (campaign.getStatus(LocalDateTime.now()) != PromotionStatus.CANCELLED) {
            throw new IllegalStateException("Only cancelled campaigns can be reactivated.");
        }
        campaign.setCancelledAt(null);
        repository.updateCampaign(campaign);
    }

    /**
     * Adds a product (from catalogue) into a campaign.
     * New schema: campaign_items stores product_id and discount_percent, promotional_price computed by repository.
     */
    public PromotionItem addItemToCampaign(long campaignId, String productId) {
        PromotionCampaign campaign = getCampaignOrThrow(campaignId);

        String trimmedId = normalizeProductId(productId);
        validateProductId(trimmedId);

        Item product = productDAO.findById(trimmedId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in catalogue: " + trimmedId));

        if (repository.itemExistsInCampaign(campaignId, trimmedId)) {
            throw new IllegalArgumentException("Product already exists in campaign: " + trimmedId);
        }

        double itemDiscount = campaign.getDiscountPercent();
        if (itemDiscount < 0 || itemDiscount > 100) {
            itemDiscount = 0.0;
        }

        PromotionItem item = new PromotionItem(0, campaignId, trimmedId, itemDiscount);

        return repository.saveItem(item);
    }

    public List<PromotionItem> getItemsByCampaign(long campaignId) {
        getCampaignOrThrow(campaignId);
        return repository.findItemsByCampaignId(campaignId);
    }

    public void recordCampaignClick(long campaignId) {
        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        validateCampaignIsActive(campaign);
        repository.incrementCampaignClick(campaignId);
    }

    public void recordItemAddedToOrder(long campaignId, long itemId, int quantity) {
        recordItemAddedToOrder(campaignId, itemId, quantity, null);
    }

    public void recordItemAddedToOrder(long campaignId, long itemId, int quantity, String orderReference) {
        validateQuantity(quantity);

        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        validateCampaignIsActive(campaign);

        PromotionItem item = getItemOrThrow(campaignId, itemId);

        repository.incrementItemAddedCount(campaignId, itemId, quantity);
        repository.savePromotionOrderEvent(
                campaignId,
                itemId,
                String.valueOf(item.getItemId()),
                "ADDED",
                quantity,
                item.getPromotionalPrice(),
                orderReference,
                LocalDateTime.now()
        );
    }

    public void recordItemPurchased(long campaignId, long itemId, int quantity) {
        recordItemPurchased(campaignId, itemId, quantity, null);
    }

    public void recordItemPurchased(long campaignId, long itemId, int quantity, String orderReference) {
        validateQuantity(quantity);

        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        validateCampaignIsActive(campaign);

        PromotionItem item = getItemOrThrow(campaignId, itemId);

        repository.incrementItemPurchasedCount(campaignId, itemId, quantity);
        repository.savePromotionOrderEvent(
                campaignId,
                itemId,
                String.valueOf(item.getItemId()),
                "PURCHASED",
                quantity,
                item.getPromotionalPrice(),
                orderReference,
                LocalDateTime.now()
        );
    }

    public PromotionCampaign updateCampaign(long campaignId,
                                            String title,
                                            String description,
                                            LocalDateTime startDateTime,
                                            LocalDateTime endDateTime,
                                            double discountPercent) {
        validateCampaignInput(title, startDateTime, endDateTime, discountPercent);

        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        campaign.setTitle(title.trim());
        campaign.setDescription(description);
        campaign.setStartDateTime(startDateTime);
        campaign.setEndDateTime(endDateTime);
        campaign.setDiscountPercent(discountPercent);

        return repository.updateCampaign(campaign);
    }

    public PromotionItem updateItem(long campaignId, long itemId, String productId) {
        getCampaignOrThrow(campaignId);

        String trimmedId = normalizeProductId(productId);
        validateProductId(trimmedId);

        productDAO.findById(trimmedId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found in catalogue: " + trimmedId));

        PromotionItem existingItem = getItemOrThrow(campaignId, itemId);

        if (!String.valueOf(existingItem.getItemId()).equals(trimmedId)
                && repository.itemExistsInCampaign(campaignId, trimmedId)) {
            throw new IllegalArgumentException("Product already exists in campaign: " + trimmedId);
        }

        existingItem.setItemId(trimmedId);
        return repository.updateItem(existingItem);
    }

    public void deleteCampaign(long campaignId) {
        getCampaignOrThrow(campaignId);
        repository.deleteCampaign(campaignId);
    }

    public void deleteItem(long itemId) {
        repository.deleteItem(itemId);
    }

    public List<SalesReportRow> getSalesReport(LocalDateTime from, LocalDateTime to) {
        validateRange(from, to);
        return repository.getSalesReport(from, to);
    }

    public List<CampaignReportRow> getCampaignReport(LocalDateTime from, LocalDateTime to) {
        validateRange(from, to);
        return repository.getCampaignReport(from, to);
    }

    public List<CampaignHitReportRow> getCampaignHitReport(LocalDateTime from, LocalDateTime to) {
        validateRange(from, to);
        return repository.getCampaignHitReport(from, to);
    }


    private PromotionCampaign getCampaignOrThrow(long campaignId) {
        return repository.findCampaignById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found with ID: " + campaignId));
    }

    private PromotionItem getItemOrThrow(long campaignId, long itemId) {
        return repository.findItemByIdAndCampaignId(itemId, campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId + " in campaign: " + campaignId));
    }

    private void validateCampaignIsActive(PromotionCampaign campaign) {
        PromotionStatus status = campaign.getStatus(LocalDateTime.now());
        if (status != PromotionStatus.ACTIVE) {
            throw new IllegalStateException("Operation requires an ACTIVE campaign. Current status: " + status);
        }
    }

    private void validateCampaignInput(String title,
                                       LocalDateTime startDateTime,
                                       LocalDateTime endDateTime,
                                       double discountPercent) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Campaign title must not be empty.");
        }
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start and end date/time must not be null.");
        }
        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("End date/time must be after start date/time.");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Campaign discount must be between 0 and 100.");
        }
    }

    private void validateProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID must not be empty.");
        }


        if (!productId.matches("\\d+")) {
            throw new IllegalArgumentException("Product ID must be numeric (catalogue ItemID).");
        }
    }

    private String normalizeProductId(String productId) {
        return productId == null ? "" : productId.trim();
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    private void validateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Period start and end are required.");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Period end must be after period start.");
        }
    }
}