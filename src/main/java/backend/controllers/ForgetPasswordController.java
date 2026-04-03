package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgetPasswordController {
    @FXML
    TextField emailInput;
    @FXML
    TextField passwordInput;
    @FXML
    TextField newPasswordInput;
    @FXML
    Label errorLabel;


    public void changePassword(ActionEvent event){
        DatabaseManager database= new DatabaseManager();
        Connection connection = database.makeConnection();

        try{
            PreparedStatement statement = connection.prepareStatement("Select * from users where email=?);");
            statement.setString(1,emailInput.getText());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){

                PreparedStatement statementTwo = connection.prepareStatement("UPDATE member SET password=? WHERE emailAddress=?");
                statementTwo.setString(1, newPasswordInput.getText());
                statementTwo.setString(2, emailInput.getText());
                statementTwo.execute();

            }
            else{
                errorLabel.setText("Email or password is wrong!");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
