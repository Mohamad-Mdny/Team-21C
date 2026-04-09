package backend.Reports;

import java.util.List;

public class CampaignStats {
    private String campaignId;
    private String startDate;
    private String endDate;
    private int itemsIncluded;
    private String discountType;
    private List<CampaignItem> items;

    public CampaignStats(String campaignId, String startDate, String endDate, int itemsIncluded, String discountType, List<CampaignItem> items) {
        this.campaignId = campaignId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.itemsIncluded = itemsIncluded;
        this.discountType = discountType;
        this.items = items;
    }

    public String getCampaignId() {
        return campaignId;
    }
    public String getStartDate() {
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public int getItemsIncluded() {
        return itemsIncluded;
    }
    public String getDiscountType() {
        return discountType;
    }
    public List<CampaignItem> getItems() {
        return items;
    }

    public double getTotalSales() {
        return items.stream().mapToDouble(CampaignItem::getTotalSales).sum();
    }
}
