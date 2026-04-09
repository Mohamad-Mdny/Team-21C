package backend.models;


import backend.communication.EmailSendResult;
import backend.communication.SendGmail;

public class Member extends User{
    private String userName;
    private String password;
    private String type;

    private String DeliveryAddress;
    private String cardNumber = "123412341234";
    private int CVV;
    private String expiryDate;
    private String BillingAddress;
    private String phoneNumber;

    public Member(String emailAddress){
        super();
        this.userName = emailAddress;
        signedIn = true;
    }

    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getExpiryDate(){return expiryDate;}
    public void setExpiryDate(String expiryDate){this.expiryDate = expiryDate;}

    public String getDeliveryAddress(){
        return DeliveryAddress;
    }
    public void setDeliveryAddress(String deliveryAddress){
        DeliveryAddress = deliveryAddress;
    }

    public String getCardNumber(){
        return cardNumber.substring(cardNumber.length() - 4);
    }
    public void setCardNumber(String cardNumber){
        this.cardNumber = cardNumber;
    }

    public int getCVV(){
        return CVV;
    }
    public void setCVV(int CVV){
        this.CVV = CVV;
    }

    public String getBillingAddress(){
        return BillingAddress;
    }
    public void setBillingAddress(String billingAddress){
        BillingAddress = billingAddress;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public boolean purchase(String deliveryAddress, String paymentMethod, String deliveryOption, String notes) {
        if (Basket == null || Basket.isEmpty()) {
//            System.out.println("Basket is empty");
            return false;
        }

        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            System.out.println("deliveryAddress is empty");
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

        for (Item item : Basket) {
            body = body + "\n   - " + item.getDescription();
        }
        body = body + "\nDelivery Address: " + deliveryAddress + "\nDelivery Option: " + deliveryOption +
                "\nNotes: " + notes ;

        body = body + "\n\nPayment Method: " + paymentMethod + "\n \n    Subtotal: £" + String.format("%.2f", getBasketSubtotal());

        EmailSendResult result = SendGmail.sendGmail(getUserName(), "Order ", body);

        Basket.clear();
        return true;
    }


}
