package backend.prm.service;

import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
import backend.prm.repository.PromotionRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains all business logic for the promotion package.
 * Data storage is fully delegated to PromotionRepository.
 *
 * Responsibilities:
 *  - Create, cancel and retrieve promotion campaigns (Admin operations)
 *  - Manage items within a campaign (Admin operations)
 *  - Track click counters per campaign (Customer interactions)
 *  - Track addedToOrder and purchased counters per item (Order events)
 *  - Calculate conversion rates for reporting
 */
public class PromotionService {

    private final PromotionRepository repository;

    public PromotionService(PromotionRepository repository) {
        this.repository = repository;
    }

    public PromotionCampaign createCampaign(String title, String description,
                                            LocalDateTime startDateTime,
                                            LocalDateTime endDateTime) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Campaign title must not be empty.");
        }
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Start and end date/time must not be null.");
        }
        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("End date/time must be after start date/time.");
        }

        PromotionCampaign campaign = new PromotionCampaign(
                0, title, description, startDateTime, endDateTime
        );
        return repository.saveCampaign(campaign);
    }


    //Cancels an active or scheduled campaign.
    public void cancelCampaign(long campaignId) {
        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        LocalDateTime now = LocalDateTime.now();

        PromotionStatus status = campaign.getStatus(now);
        if (status == PromotionStatus.CANCELLED) {
            throw new IllegalStateException("Campaign is already cancelled.");
        }
        if (status == PromotionStatus.EXPIRED) {
            throw new IllegalStateException("Campaign has already expired and cannot be cancelled.");
        }

        campaign.setCancelledAt(now);
        repository.updateCampaign(campaign);
    }

    /**
     * Returns all campaigns regardless of status.
     * Used by Admin to manage all campaigns.
     */
    public List<PromotionCampaign> getAllCampaigns() {
        return repository.findAllCampaigns();
    }


    //Returns only campaigns that are currently ACTIVE at the given time.
    public List<PromotionCampaign> getActiveCampaigns(LocalDateTime now) {
        return repository.findActiveCampaigns(now);
    }

    //Returns a single campaign by ID.

    public PromotionCampaign getCampaignById(long campaignId) {
        return getCampaignOrThrow(campaignId);
    }



    public PromotionItem addItemToCampaign(long campaignId, long productId, double discountPercent) {
        PromotionCampaign campaign = getCampaignOrThrow(campaignId);

        // Only allow adding items to campaigns that haven't started yet
        PromotionStatus status = campaign.getStatus(LocalDateTime.now());
        if (status != PromotionStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Items can only be added to SCHEDULED campaigns. Current status: " + status);
        }

        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }

        // Prevent duplicate products within the same campaign
        if (repository.itemExistsInCampaign(campaignId, productId)) {
            throw new IllegalArgumentException(
                    "Product ID " + productId + " is already in campaign " + campaignId);
        }

        PromotionItem item = new PromotionItem(0, campaignId, productId, discountPercent);
        return repository.saveItem(item);
    }


    //Returns all items belonging to a specific campaign.
    public List<PromotionItem> getItemsByCampaign(long campaignId) {
        getCampaignOrThrow(campaignId); // validate campaign exists
        return repository.findItemsByCampaignId(campaignId);
    }


    //Records a click on a campaign link by a customer.
    public void recordCampaignClick(long campaignId) {
        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        validateCampaignIsActive(campaign);
        campaign.incrementClickCount();
        repository.updateCampaign(campaign);
    }

    //Records that a customer added promotional items to an order.
    public void recordItemAddedToOrder(long campaignId, long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        PromotionCampaign campaign = getCampaignOrThrow(campaignId);
        validateCampaignIsActive(campaign);

        PromotionItem item = getItemOrThrow(campaignId, itemId);
        item.incrementAddedToOrderCount(quantity);
        repository.updateItem(item);
    }


    //Records that items from a promotional campaign were actually purchased (order paid).
    public void recordItemPurchased(long campaignId, long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        getCampaignOrThrow(campaignId);

        PromotionItem item = getItemOrThrow(campaignId, itemId);
        item.incrementPurchasedCount(quantity);
        repository.updateItem(item);
    }


    //Calculates the conversion rate for a specific promotion item.
    public double getConversionRate(long campaignId, long itemId) {
        PromotionItem item = getItemOrThrow(campaignId, itemId);
        return item.getConversionRate();
    }


    //Returns conversion rates for all items in a campaign.
    public Map<Long, Double> getCampaignConversionRates(long campaignId) {
        List<PromotionItem> items = getItemsByCampaign(campaignId);
        Map<Long, Double> rates = new HashMap<>();
        for (PromotionItem item : items) {
            rates.put(item.getId(), getConversionRate(campaignId, item.getId()));
        }
        return rates;
    }


    private PromotionCampaign getCampaignOrThrow(long campaignId) {
        return repository.findCampaignById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Campaign not found with ID: " + campaignId));
    }

    private PromotionItem getItemOrThrow(long campaignId, long itemId) {
        return repository.findItemByIdAndCampaignId(itemId, campaignId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Item not found with ID: " + itemId + " in campaign: " + campaignId));
    }

    private void validateCampaignIsActive(PromotionCampaign campaign) {
        LocalDateTime now = LocalDateTime.now();
        PromotionStatus status = campaign.getStatus(now);
        if (status != PromotionStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Operation requires an ACTIVE campaign. Current status: " + status);
        }
    }
}