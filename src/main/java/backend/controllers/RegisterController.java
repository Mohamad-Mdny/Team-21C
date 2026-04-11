package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import backend.communication.EmailSendResult;
import backend.communication.SendGmail;
import backend.interfaces.IApplicationAPI;
import backend.models.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

public class RegisterController {
    @FXML
    TextField email;
    @FXML
    PasswordField password;
    @FXML
    TextField CompanyRegistration;
    @FXML
    TextField CompanyDirector;
    @FXML
    TextField typeOfBusiness;
    @FXML
    TextField businessAddress;
    @FXML
    Label errorLabel;
    @FXML
    TextField companyName;
    //creates a member object and calls the function to register a non-commercial member while passing the required arguments
    public void submitNonCommercialApplication(ActionEvent event){
        submitNonCommercialApplication(email.getText());
        switchPage(event, "Login.fxml");
    };
    //Inserts all non-commercial user's into the database (registration)
    public void submitNonCommercialApplication(String emailAddress) {
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        String generatedPassword = generatePassword();
        if (emailCheck(emailAddress)) {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO member(emailAddress,password,type,validityStatus,totalPurchases,firstLogin) VALUES (?,?,?,?,?,?)");
                statement.setString(1, emailAddress.toLowerCase());
                statement.setString(2, generatedPassword);
                statement.setString(3, "NonCommercial");
                statement.setString(4, "valid");
                statement.setInt(5, 0);
                statement.setBoolean(6, true);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            EmailSendResult result = SendGmail.sendGmail(emailAddress, "AccountCreation", "Account Created!\n   Your random generated password is : " + generatedPassword);
        } else {
            errorLabel.setText("Invalid Email Address");
        }
    }


    public void submitCommercialApplication(ActionEvent event) {
        submitCommercialApplication(email.getText(), CompanyRegistration.getText(), CompanyDirector.getText(), typeOfBusiness.getText(), businessAddress.getText(), companyName.getText());
        switchPage(event, "Login.fxml");
    }


    // Ship to json file
    public void submitCommercialApplication(String emailAddress, String companyRegNumber, String CompanyDirector,String businessType, String businessAddress, String companyName ) {
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        //validates email
        if(emailCheck(emailAddress)){
            try {
                //Sends application details to commercial_applications table
                PreparedStatement statement = connection.prepareStatement("INSERT INTO commercial_applications(accountNo, companyName, businessAddress, companyRegistration, companyDirector, typeOfBusiness, emailAddress, status) VALUES (?,?,?,?,?,?,?,?)");
                statement.setInt(1,getApplicationCount()+1);
                statement.setString(2,companyName);
                statement.setString(3,businessAddress);
                statement.setString(4,companyRegNumber);
                statement.setString(5,CompanyDirector);
                statement.setString(6,businessType);
                statement.setString(7, emailAddress.toLowerCase());
                statement.setString(8, "SUBMITTED");
                statement.execute();
            }
            catch(SQLException e){
                e.printStackTrace();
            }
            EmailSendResult result = SendGmail.sendGmail(emailAddress, "Account validation in progress", "Your account is getting validated. This may take a while.");

        }else{
            errorLabel.setText("Invalid Email Address");
        }

    }

    //Random Password Generator
    public String generatePassword(){
        String password="";
        String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%&*?";
        Random random = new Random();
        for(int i=0; i<10 ; i++){
            int randomNumber = random.nextInt(character.length()-1);
            password = password + character.charAt(randomNumber);
        }
        System.out.println(password);
        return password;
    }
    //Functions that returns the amount of applications. Used for Incrementing account no. for new Accounts
    public int getApplicationCount(){
        int count = 0;
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM commercial_applications");
            ResultSet resultset = statement.executeQuery();
            if(resultset.next()){
                count = resultset.getInt(1);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return count;
    }
    //Email vallidation
    public boolean emailCheck(String email){
        if(email.contains("@")){
            System.out.println("valid");
            return true;
        }
        System.out.println("invalid");
        return false;
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