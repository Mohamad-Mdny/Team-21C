package backend.models;

public class Order {
    private int orderID;
    private String description;
    private String orderEmailAddress;

    public Order(int orderID, String description, String emailAddress){
        this.orderID = orderID;
        this.description = description;
        this.orderEmailAddress = emailAddress;
    }
    public int getOrderID(){
        return orderID;
    }
    public String getDescription(){
        return description;
    }
    public String getOrderEmailAddress(){
        return orderEmailAddress;
    }
}
