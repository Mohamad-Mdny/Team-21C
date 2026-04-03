package backend.prm.report;

public class SalesReportRow {
    private final String productId;
    private final String productDescription;
    private final int quantitySold;
    private final double unitPrice;
    private final double totalPrice;

    public SalesReportRow(String productId, String productDescription, int quantitySold, double unitPrice, double totalPrice) {
        this.productId = productId;
        this.productDescription = productDescription;
        this.quantitySold = quantitySold;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public String getProductId() { return productId; }
    public String getProductDescription() { return productDescription; }
    public int getQuantitySold() { return quantitySold; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
}
