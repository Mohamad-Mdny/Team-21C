package backend.models;

import backend.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transaction {
    private int transactionID;
    private double amount;
    private String transactionEmailAddress;
    private String billingAddress;
    private String purchaseDate;
    private String cardNumber;

    public Transaction(int transactionID, double amount, String emailAddress, String billingAddress,  String cardNumber, String purchaseDate){
        this.transactionID = transactionID;
        this.amount = amount;
        this.transactionEmailAddress = emailAddress;
        this.billingAddress = billingAddress;
        this.purchaseDate = purchaseDate;
        this.cardNumber = cardNumber;
    }
    public Transaction(){};

    public Transaction(int transactionID, double amount, String emailAddress){
        this.transactionID = transactionID;
        this.amount = amount;
        this.transactionEmailAddress = emailAddress;
    }
    public int getTransactionID(){
        return transactionID;
    }
    public double getAmount(){
        return amount;
    }
    public String getTransactionEmailAddress(){
        return transactionEmailAddress;
    }
    public String getBillingAddress(){
        return billingAddress;
    }
    public String getPurchaseDate(){return purchaseDate;}
    public String getCardNumber(){
        return cardNumber;
    }


    public void saveTransaction(double amount, String BillingAddress, String CardNumber, String CVV, String PurchaseDate, String emailAddress){
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO catalogue.transaction(Amount,BillingAddress, CardNumber,CVV,PurchaseDate, EmailAddress) VALUES (?,?,?,?,?,?)");
            statement.setDouble(1, amount);
            statement.setString(2, BillingAddress);
            statement.setString(3, CardNumber);
            statement.setString(4, CVV);
            statement.setString(5, PurchaseDate);
            statement.setString(6, emailAddress);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
