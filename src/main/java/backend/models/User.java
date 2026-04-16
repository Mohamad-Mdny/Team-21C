package backend.models;

import backend.EndPoints.StockServiceLayer;
import backend.communication.EmailSendResult;
import backend.communication.SendGmail;

import java.util.ArrayList;


public class User {
    ArrayList<ItemCell> Basket = new ArrayList<>();
    boolean signedIn;
    private final ArrayList<ItemCell> masterData = new ArrayList<>();

    public User() {
        signedIn = false;
    }

    public void addItem(ItemCell itemCell) {
        Basket.add(itemCell);
    }

    public void removeItem(ItemCell itemCell) {
        Basket.remove(itemCell);
    }

    public ArrayList<ItemCell> getBasket() {
        return Basket;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public void signIn() {
        this.signedIn = true;
    }

    public void signOut() {
        this.signedIn = false;
    }

    public void bringBasket(ArrayList<ItemCell> basket) {
        this.Basket = basket;
    }

    public ArrayList<ItemCell> getBasketItems() {
        return Basket;
    }


    public double getBasketSubtotal() {
        double subtotal = 0.0;

        for (ItemCell itemCell : Basket) {
            if (itemCell != null) {
                subtotal += itemCell.getPackageCost();
            }
        }

        return subtotal;
    }

    public boolean purchase(String orderId, String email, String deliveryAddress,
                            String paymentMethod, String deliveryOption, String notes) {

        if (Basket == null || Basket.isEmpty()) return false;
        for (ItemCell item : Basket) {

            boolean success =
                    StockServiceLayer.decrement(
                            Integer.toString(item.getItemID()),
                            1,
                            orderId
                    );

            if (!success) {
                return false;
            }
        }
        if (deliveryAddress == null || deliveryAddress.isBlank()) return false;
        if (paymentMethod == null || paymentMethod.isBlank()) return false;
        if (deliveryOption == null || deliveryOption.isBlank()) return false;

        StringBuilder body = new StringBuilder();
        body.append("Thank you for your purchase.")
                .append("\nOrder ID: ").append(orderId)
                .append("\n\nItems purchased:");

        for (ItemCell itemCell : Basket) {
            body.append("\n   - ").append(itemCell.getDescriptions());

        }

        body.append("\n\nDelivery Address: ").append(deliveryAddress)
                .append("\nDelivery Option: ").append(deliveryOption)
                .append("\nNotes: ").append(notes)
                .append("\n\nPayment Method: ").append(paymentMethod)
                .append("\n\nSubtotal: £").append(String.format("%.2f", getBasketSubtotal()));

        EmailSendResult result = SendGmail.sendGmail(email, "Order " + orderId, body.toString());

        return true;
    }

    public void update() {
    }

    public void clearBasket(){
        Basket.clear();
    }
}
