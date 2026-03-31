package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import backend.models.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
                PreparedStatement statement = connection.prepareStatement("SELECT emailAddress,password,firstLogin,type from member where emailAddress =?");
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()){
                    if(resultSet.getString("password").equals(password)){
                        System.out.println("successful password");
                        if(resultSet.getString("type").equals("nonCommercial")) {
                            System.out.println("non commercial login");
                            System.out.println("successful login");
                            Main.member = new Member(email);
                            Main.member.setSignedIn(true);
                            System.out.println(Main.member.getEmailAddress()+" "+Main.member.isSignedIn());
                            try{System.out.println("TRYING TO SWITCH TO CATALOGUE");
                            Parent root = FXMLLoader.load(getClass().getResource("/frontend/Catalogue.fxml"));
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stage.getScene().setRoot(root);
                            stage.show();
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }

                            if (resultSet.getBoolean("firstLogin")) {
                                System.out.println("redirect to password change");
                            } else {

                                // change to catalogue page;
                            }
                        }
                        else {
                            System.out.println("Wait for your account to get validated.");
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