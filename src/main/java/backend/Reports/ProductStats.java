package backend.Reports;

public class ProductStats {
    private String productName;
    private int quantitySold;
    private double unitPrice;
    private String itemId;

    public ProductStats(String itemId, String productName, int timesBought, double unitPrice) {
        this.itemId = itemId;
        this.productName = productName;
        this.quantitySold = timesBought;
        this.unitPrice = unitPrice;
    }

    public String getItemId() {
        return itemId;
    }
    public String getProductName() {
        return productName;
    }
    public int getQuantitySold() {
        return quantitySold;
    }
    public double getUnitPrice() {return unitPrice;}
    public double getTotalRevenue() {return getQuantitySold()  * getUnitPrice();}
}
