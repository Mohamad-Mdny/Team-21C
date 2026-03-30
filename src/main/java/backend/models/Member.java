package backend.models;


public class Member{
    private String emailAddress;

    public Member(String emailAddress ){
        this.emailAddress = emailAddress;
        }
    public String getEmailAddress(){
        return emailAddress;
    }

}
