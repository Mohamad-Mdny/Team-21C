package backend;
import java.sql.*;

public class DatabaseManager {

    public DatabaseManager(){}

    public Connection makeConnection(){
        try{
            Connection connection = DriverManager.getConnection(System.getenv("DATABASEURL"), System.getenv("USERNAME"), System.getenv("PASSWORD"));
            System.out.println("Connected to the database");
            return connection;
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return null;


    }


}
