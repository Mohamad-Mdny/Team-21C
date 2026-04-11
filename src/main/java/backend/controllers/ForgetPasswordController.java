package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

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
            PreparedStatement statement = connection.prepareStatement("Select * from member where emailAddress=?;");
            statement.setString(1,emailInput.getText());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){

                PreparedStatement statementTwo = connection.prepareStatement("UPDATE member SET password=?, firstLogin=false WHERE emailAddress=?");
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
        switchPage(event, "Login.fxml");
    }

    @FXML public void goToCatalogue(ActionEvent event) {
        switchPage(event, "Catalogue.fxml");
    }
    @FXML public void goToCurrentPromotions(ActionEvent event) {
        switchPage(event, "PromotionsPage.fxml");
    }
    @FXML public void goToBasket(ActionEvent event) {switchPage(event, "Basket.fxml");}
    @FXML public void goToLogin(ActionEvent event) {switchPage(event, "Login.fxml");}



    private void switchPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/frontend/" + fxmlFile))
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
