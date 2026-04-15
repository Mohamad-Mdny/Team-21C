package backend.models;

public class OrderCell {
    private final String OrderID;
    private final String Descriptions;
    private final String OrderEmailAddress;
    private final String DeliveryAddress;
    private String OrderStatus;

    public OrderCell(String orderID, String description, String orderEmailAddress, String DeliveryAddress) {
        this.OrderID = orderID;
        this.Descriptions = description;
        this.OrderEmailAddress = orderEmailAddress;
        this.DeliveryAddress = DeliveryAddress;

    }

    public OrderCell(String orderID, String description, String orderEmailAddress, String DeliveryAddress,String OrderStatus) {
        this.OrderID = orderID;
        this.Descriptions = description;
        this.OrderEmailAddress = orderEmailAddress;
        this.DeliveryAddress = DeliveryAddress;
        this.OrderStatus = OrderStatus;

    }

    public String getOrderID() {
        return OrderID;
    }
    public String getDescriptions() {
        return Descriptions;
    }
    public String getOrderEmailAddress() {
        return OrderEmailAddress;
    }
    public String getDeliveryAddress() {
        return DeliveryAddress;
    }
    public String getOrderStatus() {return OrderStatus;}

}
