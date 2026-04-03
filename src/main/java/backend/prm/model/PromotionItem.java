package backend.prm.model;

public class PromotionItem {
    private long id;
    private long campaignId;

    private String itemId;

    private double discountPercent;
    private double promotionalPrice;
    private int addedToOrderCount;
    private int purchasedCount;

    public PromotionItem(long id, long campaignId, String itemId, double discountPercent, double promotionalPrice) {
        this.id = id;
        this.campaignId = campaignId;
        this.itemId = itemId;
        this.discountPercent = discountPercent;
        this.promotionalPrice = promotionalPrice;
    }

    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
    public double getPromotionalPrice() { return promotionalPrice; }
    public void setPromotionalPrice(double promotionalPrice) {
        this.promotionalPrice = promotionalPrice;
    }
    public int getAddedToOrderCount() { return addedToOrderCount; }
    public void setAddedToOrderCount(int addedToOrderCount) {
        this.addedToOrderCount = addedToOrderCount;
    }
    public int getPurchasedCount() { return purchasedCount; }
    public void setPurchasedCount(int purchasedCount) { this.purchasedCount = purchasedCount; }
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCampaignId() { return campaignId; }
    public void setCampaignId(long campaignId) { this.campaignId = campaignId; }


    // keep other getters/setters as-is
}