package backend.models;

public class Admin extends User{
    private String username;
    public Admin(String username) {
        super();
        this.username = username;
        signedIn = true;
    }

}
