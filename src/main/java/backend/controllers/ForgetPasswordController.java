package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ForgetPasswordController {
    @FXML
    TextField emailInput;
    @FXML
    TextField passwordInput;
    @FXML
    TextField newPasswordInput;


    public void changePassword(ActionEvent event){
        DatabaseManager database= new DatabaseManager();
        Connection connection = database.makeConnection();

        try{
            PreparedStatement statement = connection.prepareStatement("UPDATE member SET password=? WHERE emailAddress=?");
            statement.setString(1, newPasswordInput.getText());
            statement.setString(2, emailInput.getText());
            statement.execute();
            System.out.println("password has been changed");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
