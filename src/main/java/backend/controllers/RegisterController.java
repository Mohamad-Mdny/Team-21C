package backend.controllers;

import backend.DatabaseManager;
import backend.communication.EmailSendResult;
import backend.communication.SendGmail;
import backend.interfaces.IApplicationAPI;
import backend.models.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    //creates a member object and calls the function to register a non-commercial member while passing the required arguments
    public void submitNonCommercialApplication(ActionEvent event){
        submitNonCommercialApplication(email.getText());

    };

    //creates a member object and calls the function to register a commercial member while passing the required arguments
    public void submitCommercialApplication(ActionEvent event) {
        submitCommercialApplication(email.getText(), Integer.parseInt(CompanyRegistration.getText()), CompanyDirector.getText(), typeOfBusiness.getText(), businessAddress.getText());
    }

    public void submitCommercialApplication(String emailAddress, int companyRegNumber, String CompanyDirector,String businessType, String businessAddress ) {
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        if(emailCheck(emailAddress)){
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO member(emailAddress,type,validityStatus,CompanyRegistration, CompanyDirector, typeOfBusiness,  businessAddress) VALUES (?,?,?,?,?,?,?)");
                statement.setString(1,emailAddress.toLowerCase());
                statement.setString(2,"Commercial");
                statement.setString(3,"invalid");
                statement.setInt(4,companyRegNumber);
                statement.setString(5,CompanyDirector);
                statement.setString(6,businessType);
                statement.setString(7, businessAddress);
                statement.execute();
            }
            catch(SQLException e){
                e.printStackTrace();
            }
            EmailSendResult result = SendGmail.sendGmail("mateusz.niedbalski@city.ac.uk", "Account validation in progress", "Your account is getting validated. This may take a while.");
        }else{
            errorLabel.setText("Invalid Email Address");
        }

    }
    //Inserts all non-commercial user's into the database (registration)
    public void submitNonCommercialApplication(String emailAddress){
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        String generatedPassword=generatePassword();
        if(emailCheck(emailAddress)){
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO member(emailAddress,password,type,validityStatus,totalPurchases,firstLogin) VALUES (?,?,?,?,?,?)");
            statement.setString(1,emailAddress.toLowerCase());
            statement.setString(2,generatedPassword);
            statement.setString(3,"nonCommercial");
            statement.setString(4,"valid");
            statement.setInt(5,0);
            statement.setBoolean(6,true);
            statement.execute();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
            EmailSendResult result = SendGmail.sendGmail("mateusz.niedbalski@city.ac.uk", "AccountCreation", "Account Created!\n Your random generated password is:"+generatedPassword);
        }
        else{
            errorLabel.setText("Invalid Email Address");
        }
    }
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
    public boolean emailCheck(String email){
        if(email.contains("@")){
            System.out.println("valid");
            return true;
        }
        System.out.println("invalid");
        return false;
    }
}