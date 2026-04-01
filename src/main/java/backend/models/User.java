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

    public void setSignedIn(){
        this.signedIn=true;
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
        String body = "Thank you for your purchase. " +
                "\nItems purchased:";

        for (Item item : Basket) {
            body = body + "\n   - " + item.getDescription();
        }
        body = body + "\nDelivery Address: " + deliveryAddress + "\nDelivery Option: " + deliveryOption +
                "\nNotes: " + notes ;

        body = body + "\n\nPayment Method: " + paymentMethod + "\n \n    Subtotal: £" + String.format("%.2f", getBasketSubtotal());

        EmailSendResult result = SendGmail.sendGmail("surya.premkumar@city.ac.uk", "Order ", body);

        Basket.clear();
        return true;
    }

    public void update(){
    }
}
