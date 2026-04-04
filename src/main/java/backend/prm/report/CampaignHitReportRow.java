package backend.prm.report;

public class CampaignHitReportRow {
    private final String campaignCode;
    private final String campaignTitle;
    private final String productId;
    private final String productDescription;
    private final int campaignClicks;
    private final int addedToOrder;
    private final int purchased;
    private final double conversionRate;

    public CampaignHitReportRow(String campaignCode,
                                String campaignTitle,
                                String productId,
                                String productDescription,
                                int campaignClicks,
                                int addedToOrder,
                                int purchased,
                                double conversionRate) {
        this.campaignCode = campaignCode;
        this.campaignTitle = campaignTitle;
        this.productId = productId;
        this.productDescription = productDescription;
        this.campaignClicks = campaignClicks;
        this.addedToOrder = addedToOrder;
        this.purchased = purchased;
        this.conversionRate = conversionRate;
    }

    public String getCampaignCode() { return campaignCode; }
    public String getCampaignTitle() { return campaignTitle; }
    public String getProductId() { return productId; }
    public String getProductDescription() { return productDescription; }
    public int getCampaignClicks() { return campaignClicks; }
    public int getAddedToOrder() { return addedToOrder; }
    public int getPurchased() { return purchased; }
    public double getConversionRate() { return conversionRate; }
}
