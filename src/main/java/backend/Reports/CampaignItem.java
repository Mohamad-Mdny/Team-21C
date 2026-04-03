package backend.Reports;

public class CampaignItem {
    private String itemId;
    private String description;
    private String discount;
    private int itemSold;
    private double totalSales;

    public CampaignItem(String itemId, String description, String discount, int itemSold, double totalSales) {
        this.itemId = itemId;
        this.description = description;
        this.discount = discount;
        this.itemSold = itemSold;
        this.totalSales = totalSales;
    }

    public String getItemId() {
        return itemId;
    }
    public String getDescription() {
        return description;
    }
    public String getDiscount() {
        return discount;
    }
    public int getItemSold() {
        return itemSold;
    }
    public double getTotalSales() {
        return totalSales;
    }

}
