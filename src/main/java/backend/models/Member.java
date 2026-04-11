package backend.models;


import backend.DatabaseManager;
import backend.communication.EmailSendResult;
import backend.communication.SendGmail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Member extends User {
    private String userName;
    private String password;
    private String type;

    private String DeliveryAddress;
    private String cardNumber = "123412341234";
    private int CVV;
    private String expiryDate;
    private String BillingAddress;
    private String phoneNumber;

    public Member(String emailAddress) {
        super();
        this.userName = emailAddress;
        signedIn = true;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        DeliveryAddress = deliveryAddress;
    }

    public String getCardNumber() {
        return cardNumber.substring(cardNumber.length() - 4);
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCVV() {
        return CVV;
    }

    public void setCVV(int CVV) {
        this.CVV = CVV;
    }

    public String getBillingAddress() {
        return BillingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        BillingAddress = billingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean purchase(String OrderID, String deliveryAddress, String paymentMethod, String deliveryOption, String notes) {
        //Safe checks
        if (Basket == null || Basket.isEmpty()) {
//            System.out.println("Basket is empty");
            return false;
        }
        if (deliveryAddress == null || deliveryAddress.isBlank()) {
//            System.out.println("deliveryAddress is empty");
            return false;
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
//            System.out.println("paymentMethod is empty");
            return false;
        }
        if (deliveryOption == null || deliveryOption.isBlank()) {
//            System.out.println("deliveryOption is empty");
            return false;
        }

        String body = "Thank you for your purchase. " +
                "\nItems purchased:";

        for (ItemCell itemCell : Basket) {
            body = body + "\n   - " + itemCell.getDescriptions();
        }
        body = body + "\nDelivery Address: " + deliveryAddress +
                "\nDelivery Option: " + deliveryOption +
                "\nNotes: " + notes;

        body = body + "\n\nPayment Method: " + paymentMethod +
                "\n \n    Subtotal: £" + String.format("%.2f", getBasketSubtotal());

        EmailSendResult result = SendGmail.sendGmail(getUserName(), "Order " + OrderID, body);

        Basket.clear();
        return true;
    }

    //gathers the number of purchases made by the member that is logged in
    //if the number of purchases is divisible by 10 then it will return true else false
    public boolean checkMemberDiscount(String emailAddress) {
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        int currentPurchaseCount;
        int nextPurchaseCount;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT totalPurchases FROM member WHERE emailAddress=?");
            statement.setString(1, emailAddress);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currentPurchaseCount = resultSet.getInt("totalPurchases");
                nextPurchaseCount = currentPurchaseCount + 1;
                if (nextPurchaseCount % 10 == 0) {
                    System.out.println("Discount Active");
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //this finds the number of purchases made by the member
    //updates the table to increments total purchases by 1
    public void incrementMemberPurchases(String emailAddress) {
        int totalPurchases = 0;
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT totalPurchases FROM member WHERE emailAddress=?");
            statement.setString(1, emailAddress);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalPurchases = resultSet.getInt("totalPurchases");
            }
            PreparedStatement statementTwo = connection.prepareStatement("UPDATE member SET totalPurchases=? WHERE emailAddress=?");
            statementTwo.setInt(1, totalPurchases + 1);
            statementTwo.setString(2, emailAddress);
            statementTwo.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
