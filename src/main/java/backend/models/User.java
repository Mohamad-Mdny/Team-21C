package backend.models;

import backend.DatabaseManager;
import backend.communication.EmailSendResult;
import backend.communication.SendGmail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class User {
    ArrayList<Item> Basket = new ArrayList<>();
    boolean signedIn;
    private final ArrayList<Item> masterData = new ArrayList<>();

    public User() {
        signedIn = false;
        /*
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();
        if (connection == null) {
            System.out.println("Database connection failed.");
            return;
        }
        String sql = "SELECT * FROM Catalogue";
        try (connection;
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                masterData.add(new Item(resultSet.getInt("ItemID"),
                        resultSet.getString("Description"),
                        resultSet.getString("PackageType"),
                        resultSet.getString("Unit"),
                        resultSet.getInt("UnitsInAPack"),
                        resultSet.getFloat("PackageCost"),
                        resultSet.getInt("Availability"),
                        resultSet.getInt("StockLimit"))
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

         */
    }

    public void addItem(Item item) {
        Basket.add(item);
    }

    public void removeItem(Item item) {
        Basket.remove(item);
    }

    public ArrayList<Item> getBasket() {
        return Basket;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public void setSignedIn(boolean signedIn){
        this.signedIn=signedIn;
    }

    public double getBasketSubtotal() {
        double subtotal = 0.0;

        for (Item item : Basket) {
            if (item != null) {
                subtotal += item.getPackageCost();
            }
        }

        return subtotal;
    }

    public boolean purchase(String deliveryAddress, String paymentMethod, String deliveryOption, String notes) {
        if (Basket == null || Basket.isEmpty()) {
            return false;
        }

        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            return false;
        }

        if (paymentMethod == null || paymentMethod.isBlank()) {
            return false;
        }

        if (deliveryOption == null || deliveryOption.isBlank()) {
            return false;
        }
        String body = "Thank you for your purchase. \nDelivery Address: " + deliveryAddress + "\nDelivery Option: " + deliveryOption +
                "\nNotes: " + notes +
                "\nItems purchased:";

        for (Item item : Basket) {
            body = body + "\n   - " + item.getDescription();
        }
        body = body + "\n\nPayment Method: " + paymentMethod + "\n \n    Subtotal: £" + String.format("%.2f", getBasketSubtotal());

        EmailSendResult result = SendGmail.sendGmail("surya.premkumar@city.ac.uk", "Order ", body);

        Basket.clear();
        return true;
    }
}
