package backend;
import java.sql.*;

public class DatabaseManager {

    public DatabaseManager(){}

    public Connection makeConnection(){
        try{
            Connection connection = DriverManager.getConnection(System.getenv("DATABASEURL"), System.getenv("USERNAME"), System.getenv("PASSWORD"));

            return connection;
        }
        catch (SQLException e){
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }

        return null;


    }


}