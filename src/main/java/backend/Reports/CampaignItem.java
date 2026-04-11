package backend.Reports;

public class CampaignItem {
    private String itemId;
    private String descriptions;
    private String discount;
    private int itemSold;
    private double totalSales;

    public CampaignItem(String itemId, String descriptions, String discount, int itemSold, double totalSales) {
        this.itemId = itemId;
        this.descriptions = descriptions;
        this.discount = discount;
        this.itemSold = itemSold;
        this.totalSales = totalSales;
    }

    public String getItemId() {
        return itemId;
    }
    public String getDescriptions() {
        return descriptions;
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
