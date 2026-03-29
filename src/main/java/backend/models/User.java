package backend.models;

import java.util.ArrayList;

public class User {
    ArrayList<Item> Basket = new ArrayList<>();
    boolean signedIn;

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

        System.out.println("Purchase successful");
        System.out.println("Delivery Address: " + deliveryAddress);
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Delivery Option: " + deliveryOption);
        System.out.println("Notes: " + notes);
        System.out.println("Items purchased: " + Basket.size());
        System.out.println("Subtotal: £" + String.format("%.2f", getBasketSubtotal()));

        Basket.clear();
        return true;
    }
}