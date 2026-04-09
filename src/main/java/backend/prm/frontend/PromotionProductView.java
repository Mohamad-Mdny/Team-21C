package backend.prm.frontend;

public class PromotionProductView {

    private final long itemId;
    private final long campaignId;
    private final String productId;
    private final String productName;
    private final double originalPrice;
    private final double discountPercent;
    private final double discountedPrice;
    private final int addedToOrderCount;
    private final int purchasedCount;

    public PromotionProductView(long itemId,
                                long campaignId,
                                String productId,
                                String productName,
                                double originalPrice,
                                double discountPercent,
                                int addedToOrderCount,
                                int purchasedCount) {
        this.itemId = itemId;
        this.campaignId = campaignId;
        this.productId = productId;
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.discountPercent = discountPercent;
        this.discountedPrice = originalPrice - (originalPrice * discountPercent / 100.0);
        this.addedToOrderCount = addedToOrderCount;
        this.purchasedCount = purchasedCount;
    }

    public long getItemId() { return itemId; }
    public long getCampaignId() { return campaignId; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getOriginalPrice() { return originalPrice; }
    public double getDiscountPercent() { return discountPercent; }
    public double getDiscountedPrice() { return discountedPrice; }
    public int getAddedToOrderCount() { return addedToOrderCount; }
    public int getPurchasedCount() { return purchasedCount; }
}
