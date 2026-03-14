package backend.models;
import java.sql.*;
import backend.DatabaseManager;

public class Member {
    public Member(){


    }
    public void createMember(){
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO member(emailAddress,password,type,validityStatus) VALUES (?,?,?,?)");
            statement.setString(1,"TestEmail");
            statement.setString(2,"TestPassword");
            statement.setString(3,"TestType");
            statement.setString(4,"valid");
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }


    }
}
