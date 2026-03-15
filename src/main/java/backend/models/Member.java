package backend.models;
import java.sql.*;
import backend.DatabaseManager;
import backend.interfaces.IApplicationAPI;

public class Member implements IApplicationAPI {
    public Member(){}


    //Inserts all non-commercial user's into the database (registration)
    public void submitNonCommercialApplication(String email){
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO member(emailAddress,password,type,validityStatus,totalPurchases,firstLogin) VALUES (?,?,?,?,?,?)");
            statement.setString(1,email);
            statement.setString(2,"GeneratedPassword");
            statement.setString(3,"nonCommercial");
            statement.setString(4,"valid");
            statement.setInt(5,0);
            statement.setBoolean(6,true);
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }


    };
    //Inserts all commercial user's into the database (registration)
    @Override
    public void submitCommercialApplication(String emailAddress, String password, int companyRegNumber, String CompanyDirector,String businessType, String businessAddress ) {
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO member(emailAddress,password,type,validityStatus,CompanyRegistration, CompanyDirector, typeOfBusiness,  businessAddress) VALUES (?,?,?,?,?,?,?,?)");
            statement.setString(1,emailAddress);
            statement.setString(2,password);
            statement.setString(3,"nonCommercial");
            statement.setString(4,"valid");
            statement.setInt(5,companyRegNumber);
            statement.setString(6,CompanyDirector);
            statement.setString(7,businessType);
            statement.setString(8, businessAddress);

            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }


    }

    @Override
    public void sendEmail(String recipientEmailAddress, String message, String subject) {

    }

    @Override
    public void getDeliveryStatus(int messageID) {

    }
}
