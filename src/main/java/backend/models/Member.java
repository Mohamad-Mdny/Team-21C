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

    public Member(String emailAddress){
        super();
        this.emailAddress = emailAddress;
        signedIn = true;
    }



    public String getEmailAddress(){
        return emailAddress;
    }

}
