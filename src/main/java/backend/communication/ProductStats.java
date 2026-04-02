package backend.communication;

public class ProductStats {
    private String productName;
    private int timesBought;

    public ProductStats(String productName, int timesBought) {
        this.productName = productName;
        this.timesBought = timesBought;
    }

    public String getProductName() {
        return productName;
    }
    public int getTimesBought() {
        return timesBought;
    }
}
