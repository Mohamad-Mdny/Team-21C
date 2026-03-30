package backend.controllers;

import backend.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    TextField emailInput;

    @FXML
    TextField passwordInput;

    public void login(ActionEvent event){
        String email = emailInput.getText();
        String password = passwordInput.getText();

        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();
        if (connection != null){
            System.out.println("Connection Successful");
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT emailAddress,password,firstLogin from member where emailAddress =?");
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()){
                    if(resultSet.getString("password").equals(password)){
                        System.out.println("successful password");
                        if(resultSet.getBoolean("firstLogin")){
                            System.out.println("redirect to password change");
                        }
                        else{
                            System.out.println("successful login");
                        }
                    }
                }
                else{
                    System.out.println("username is not found");
                }
            }
            catch (SQLException e){
                e.printStackTrace();
            }


        }

    }

}
