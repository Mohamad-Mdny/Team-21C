package backend.prm.repository;

import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.report.CampaignHitReportRow;
import backend.prm.report.CampaignReportRow;
import backend.prm.report.SalesReportRow;

import java.time.LocalDateTime;
import java.util.*;

public class FakePromotionRepository extends PromotionRepository {

    private final Map<Long, PromotionCampaign> campaigns = new HashMap<>();
    private final Map<Long, PromotionItem> items = new HashMap<>();
    private final Set<String> clickedCampaigns = new HashSet<>();
    private final Map<Long, Integer> itemAddedCounts = new HashMap<>();
    private final Map<Long, Integer> itemPurchasedCounts = new HashMap<>();

    private long nextCampaignId = 1;
    private long nextItemId = 1;

    @Override
    public PromotionCampaign saveCampaign(PromotionCampaign campaign) {
        campaign.setId(nextCampaignId++);
        campaign.setCampaignCode("CAMP_" + String.format("%03d", campaign.getId()));
        campaigns.put(campaign.getId(), campaign);
        return campaign;
    }

    @Override
    public Optional<PromotionCampaign> findCampaignById(long id) {
        return Optional.ofNullable(campaigns.get(id));
    }

    @Override
    public List<PromotionCampaign> findAllCampaigns() {
        return new ArrayList<>(campaigns.values());
    }

    @Override
    public List<PromotionCampaign> findActiveCampaigns(LocalDateTime now) {
        List<PromotionCampaign> result = new ArrayList<>();
        for (PromotionCampaign c : campaigns.values()) {
            if (c.getCancelledAt() == null
                    && !now.isBefore(c.getStartDateTime())
                    && !now.isAfter(c.getEndDateTime())) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public PromotionCampaign updateCampaign(PromotionCampaign campaign) {
        campaigns.put(campaign.getId(), campaign);
        return campaign;
    }

    @Override
    public void incrementCampaignClick(long campaignId) {
        PromotionCampaign c = campaigns.get(campaignId);
        if (c != null) c.setClickCount(c.getClickCount() + 1);
    }

    @Override
    public void deleteCampaign(long campaignId) {
        campaigns.remove(campaignId);
        items.entrySet().removeIf(e -> e.getValue().getCampaignId() == campaignId);
    }

    @Override
    public boolean campaignExists(long campaignId) {
        return campaigns.containsKey(campaignId);
    }

    @Override
    public PromotionItem saveItem(PromotionItem item) {
        item.setId(nextItemId++);
        item.setPromotionalPrice(9.99); // dummy price — no DB needed
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public PromotionItem updateItem(PromotionItem item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void incrementItemAddedCount(long campaignId, long itemId, int quantity) {
        itemAddedCounts.merge(itemId, quantity, Integer::sum);
    }

    @Override
    public void incrementItemPurchasedCount(long campaignId, long itemId, int quantity) {
        itemPurchasedCounts.merge(itemId, quantity, Integer::sum);
    }

    @Override
    public void savePromotionOrderEvent(long campaignId, long itemId, String productId,
                                        String eventType, int quantity, double unitPrice,
                                        String orderReference, LocalDateTime eventTime) {
    }

    @Override
    public Optional<PromotionItem> findItemById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<PromotionItem> findItemsByCampaignId(long campaignId) {
        List<PromotionItem> result = new ArrayList<>();
        for (PromotionItem item : items.values()) {
            if (item.getCampaignId() == campaignId) result.add(item);
        }
        return result;
    }

    @Override
    public Optional<PromotionItem> findItemByIdAndCampaignId(long itemId, long campaignId) {
        PromotionItem item = items.get(itemId);
        if (item != null && item.getCampaignId() == campaignId) return Optional.of(item);
        return Optional.empty();
    }

    @Override
    public boolean itemExistsInCampaign(long campaignId, String productId) {
        for (PromotionItem item : items.values()) {
            if (item.getCampaignId() == campaignId && item.getItemId().equals(productId)) return true;
        }
        return false;
    }

    @Override
    public void deleteItem(long itemId) {
        items.remove(itemId);
    }

    @Override
    public List<SalesReportRow> getSalesReport(LocalDateTime from, LocalDateTime to) {
        return new ArrayList<>();
    }

    @Override
    public List<CampaignReportRow> getCampaignReport(LocalDateTime from, LocalDateTime to) {
        return new ArrayList<>();
    }

    @Override
    public List<CampaignHitReportRow> getCampaignHitReport(LocalDateTime from, LocalDateTime to) {
        return new ArrayList<>();
    }

    @Override
    public void refreshPromotionalPricesForCampaignItemsWithoutOverride(long campaignId) {}

    // Helper for tests to check counters
    public int getAddedCount(long itemId) {
        return itemAddedCounts.getOrDefault(itemId, 0);
    }

    public int getPurchasedCount(long itemId) {
        return itemPurchasedCounts.getOrDefault(itemId, 0);
    }
}