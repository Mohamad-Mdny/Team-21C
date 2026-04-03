package backend.communication;

public class ProductStats {
    private String productName;
    private int quantitySold;
    private double unitPrice;

    public ProductStats(String productName, int timesBought, double unitPrice) {
        this.productName = productName;
        this.quantitySold = timesBought;
        this.unitPrice = unitPrice;
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
