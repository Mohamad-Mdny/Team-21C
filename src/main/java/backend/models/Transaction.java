package backend.models;

import backend.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transaction {
    private int transactionID;
    private int amount;
    private String transactionEmailAddress;

    public Transaction(int transactionID, int amount, String emailAddress){
        this.transactionID = transactionID;
        this.amount = amount;
        this.transactionEmailAddress = emailAddress;
    }
    public Transaction(){};

    public int getTransactionID(){
        return transactionID;
    }
    public int getAmount(){
        return amount;
    }
    public String getTransactionEmailAddress(){
        return transactionEmailAddress;
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
