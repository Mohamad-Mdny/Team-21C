package backend.models;

public class ItemCell {
    private int ItemID;
    private String Descriptions;
    private String PackageType;
    private String Unit;
    private int UnitsInAPack;
    private float PackCost;
    private int Availability;
    private float StockLimit;


    //constructor for item class
    public ItemCell(int ItemID, String Description, String PackageType, String Unit, int UnitsInAPack, float PackCost, int Availability, float StockLimit){
        this.ItemID = ItemID;
        this.Descriptions = Description;
        this.PackageType = PackageType;
        this.Unit = Unit;
        this.UnitsInAPack = UnitsInAPack;
        this.PackCost = PackCost;
        this.Availability = Availability;
        this.StockLimit = StockLimit;
    }

    public String getItemID() {return Integer.toString(ItemID);}
    public String getDescriptions() {return Descriptions;}
    public String getPackageType() {return PackageType;}
    public String getUnit(){return Unit;}
    public int getUnitsInAPack(){return UnitsInAPack;}
    public float getPackageCost(){return PackCost;}
    public int getAvailability(){return Availability;}
    public float getStockLimit(){return StockLimit;}
}
