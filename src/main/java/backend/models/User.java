package backend.models;

import backend.communication.EmailSendResult;
import backend.communication.SendGmail;

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

    public void signIn(){
        this.signedIn=true;
    }

    public void signOut(){
        this.signedIn=false;
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

    public boolean purchase(String email, String deliveryAddress, String paymentMethod, String deliveryOption, String notes) {
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

        EmailSendResult result = SendGmail.sendGmail(email, "Order ", body);

        Basket.clear();
        return true;
    }

    public void update(){
    }
}
