package backend.prm.model;



public class PromotionItem {

    private long id;
    private long campaignId;
    private long productId;
    private double discountPercent;
    private int addedToOrderCount;
    private int purchasedCount;

    public PromotionItem() {
    }

    public PromotionItem(long id, long campaignId, long productId, double discountPercent) {
        this.id = id;
        this.campaignId = campaignId;
        this.productId = productId;
        this.discountPercent = discountPercent;
        this.addedToOrderCount = 0;
        this.purchasedCount = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getAddedToOrderCount() {
        return addedToOrderCount;
    }

    public void setAddedToOrderCount(int addedToOrderCount) {
        this.addedToOrderCount = addedToOrderCount;
    }

    public int getPurchasedCount() {
        return purchasedCount;
    }

    public void setPurchasedCount(int purchasedCount) {
        this.purchasedCount = purchasedCount;
    }

    public void incrementAddedToOrderCount(int quantity) {
        this.addedToOrderCount += quantity;
    }

    public void incrementPurchasedCount(int quantity) {
        this.purchasedCount += quantity;
    }
    public double getConversionRate() {
        if (addedToOrderCount == 0) return 0.0;
        return (double) purchasedCount / addedToOrderCount;
    }
}