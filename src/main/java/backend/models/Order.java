package backend.models;

import backend.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Order {
    private int orderID;
    private String description;
    private String orderEmailAddress;

    public Order(int orderID, String description, String emailAddress){
        this.orderID = orderID;
        this.description = description;
        this.orderEmailAddress = emailAddress;
    }
    public Order(){}
    public int getOrderID(){
        return orderID;
    }
    public String getDescription(){
        return description;
    }
    public String getOrderEmailAddress(){
        return orderEmailAddress;
    }

    //Save Order
    public void saveOrder(String description, String Address, String DeliveryType, String emailAddress){
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO catalogue.order(Description, Address,DeliveryType,OrderStatus,EmailAddress) VALUES (?,?,?,?,?)");
            statement.setString(1, description);
            statement.setString(2, Address);
            statement.setString(3, DeliveryType);
            statement.setString(4, "Pending");
            statement.setString(5, emailAddress);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






}