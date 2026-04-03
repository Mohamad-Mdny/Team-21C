package backend.prm.model;

public class Product {

    private String productId;
    private int merchantId;
    private String description;
    private String packageType;
    private String unit;
    private int unitsInPack;
    private double packageCost;
    private int availabilityPacks;
    private int stockLimitPacks;
    private boolean active;

    public Product() {
    }

    public Product(String productId, int merchantId, String description, String packageType,
                   String unit, int unitsInPack, double packageCost,
                   int availabilityPacks, int stockLimitPacks, boolean active) {
        this.productId = productId;
        this.merchantId = merchantId;
        this.description = description;
        this.packageType = packageType;
        this.unit = unit;
        this.unitsInPack = unitsInPack;
        this.packageCost = packageCost;
        this.availabilityPacks = availabilityPacks;
        this.stockLimitPacks = stockLimitPacks;
        this.active = active;
    }

    public String getProductId() {
        return productId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public String getDescription() {
        return description;
    }

    public String getPackageType() {
        return packageType;
    }

    public String getUnit() {
        return unit;
    }

    public int getUnitsInPack() {
        return unitsInPack;
    }

    public double getPackageCost() {
        return packageCost;
    }

    public int getAvailabilityPacks() {
        return availabilityPacks;
    }

    public int getStockLimitPacks() {
        return stockLimitPacks;
    }

    public boolean isActive() {
        return active;
    }
}