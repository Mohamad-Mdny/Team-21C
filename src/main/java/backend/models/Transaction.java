package backend.models;

public class Transaction {
    private int transactionID;
    private int amount;
    private String transactionEmailAddress;
    public Transaction(int transactionID, int amount, String emailAddress){
        this.transactionID = transactionID;
        this.amount = amount;
        this.transactionEmailAddress = emailAddress;
    }
    public int getTransactionID(){
        return transactionID;
    }
    public int getAmount(){
        return amount;
    }
    public String getTransactionEmailAddress(){
        return transactionEmailAddress;
    }
}
