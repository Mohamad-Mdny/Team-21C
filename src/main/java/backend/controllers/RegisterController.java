package backend.controllers;

import backend.interfaces.IApplicationAPI;
import backend.models.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
    //creates a member object and calls the function to register a non-commercial member while passing the required arguments
    public void submitNonCommercialApplication(ActionEvent event){
        Member member = new Member();
        member.submitNonCommercialApplication(email.getText());
    };

    //creates a member object and calls the function to register a commercial member while passing the required arguments
    public void submitCommercialApplication(ActionEvent event) {
        Member member = new Member();
        member.submitCommercialApplication(email.getText(), password.getText(),Integer.parseInt(CompanyRegistration.getText()), CompanyDirector.getText(), typeOfBusiness.getText(), businessAddress.getText());
    }
}
