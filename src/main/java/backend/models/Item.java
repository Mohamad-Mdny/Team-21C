package backend.models;

public class Item {
    private int ItemID;
    private String Description;
    private String PackageType;
    private String Unit;
    private int UnitsInAPack;
    private float PackCost;
    private int Availability;
    private float StockLimit;
    //constructor for item class
    public Item(int ItemID,String Description,String PackageType,String Unit, int UnitsInAPack, float PackCost, int Availability, float StockLimit){
        this.ItemID = ItemID;
        this.Description = Description;
        this.PackageType = PackageType;
        this.Unit = Unit;
        this.UnitsInAPack = UnitsInAPack;
        this.PackCost = PackCost;
        this.Availability = Availability;
        this.StockLimit = StockLimit;
    }
    //getters for the item class
    public int getItemID(){
        return ItemID;
    }
    public String getDescription(){
        return Description;
    }
    public String getPackageType(){
        return PackageType;
    }
    public String getUnit(){
        return Unit;
    }
    public int getUnitsInAPack(){
        return UnitsInAPack;
    }
    public float getPackageCost(){
        return PackCost;
    }
    public int getAvailability(){
        return Availability;
    }
    public float getStockLimit(){
        return StockLimit;
    }
}
