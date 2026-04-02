package backend.models;


public class Member extends User{
    private String emailAddress;
    private String password;
    private String type;
    private String validityStatus;
    private String CompanyRegistration;
    private String CompanyDirector;
    private String typeOfBusiness;
    private String businessAddress;

    private String DeliveryAddress;
    private String cardNumber = "123412341234";
    private int CVV;
    private String BillingAddress;
    private String phoneNumber;

    public Member(String emailAddress){
        super();
        this.emailAddress = emailAddress;
        signedIn = true;
    }

    public String getEmailAddress(){
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
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

    public String getValidityStatus(){
        return validityStatus;
    }
    public void setValidityStatus(String validityStatus){
        this.validityStatus = validityStatus;
    }

    public String getCompanyRegistration(){
        return CompanyRegistration;
    }
    public void setCompanyRegistration(String companyRegistration){
        CompanyRegistration = companyRegistration;
    }
    public String getCompanyDirector(){
        return CompanyDirector;

    }
    public void setCompanyDirector(String companyDirector){
        CompanyDirector = companyDirector;
    }
    public String getTypeOfBusiness(){
        return typeOfBusiness;
    }
    public void setTypeOfBusiness(String typeOfBusiness){
        this.typeOfBusiness = typeOfBusiness;
    }
    public String getBusinessAddress(){
        return businessAddress;
    }
    public void setBusinessAddress(String businessAddress){
        this.businessAddress = businessAddress;
    }

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




}
