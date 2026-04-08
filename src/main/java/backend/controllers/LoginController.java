package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import backend.models.Admin;
import backend.models.Member;
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


public class LoginController {

    // back end is mateusz stuff, idk how it works, i (surya) just added some front end buttons
    @FXML
    private TextField searchField;
    @FXML
    TextField emailInput;
    @FXML
    TextField passwordInput;
    @FXML
    Label errorLabel;

    public void login(ActionEvent event) {
        System.out.println("Attempting to login");
        String email = emailInput.getText().toLowerCase();
        System.out.println(email);
        String password = passwordInput.getText();
        System.out.println(password);

        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();
        if (connection != null) {
            System.out.println("connect success");
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT emailAddress,password,firstLogin,type from member where emailAddress =?");
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {

                    if (resultSet.getString("password").equals(password)) {


                        switch (resultSet.getString("type")) {
                            case "NonCommercial": {
                                if (resultSet.getBoolean("firstLogin")) {
                                    try {
                                        Parent root = FXMLLoader.load(getClass().getResource("/frontend/ForgetPassword.fxml"));
                                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                        stage.getScene().setRoot(root);
                                        stage.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    try {
                                        Main.member = new Member(email);
                                        Main.m.signIn();
                                        Main.member.bringBasket(Main.m.getBasket());

                                        Parent root = FXMLLoader.load(getClass().getResource("/frontend/Catalogue.fxml"));
                                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                        stage.getScene().setRoot(root);
                                        stage.show();
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return;
                            }
                            case "Administrator", "PU-Admin": {
                                try {
                                    Main.admin = new Admin(email);
                                    Main.m.signIn();

                                    Parent root = FXMLLoader.load(getClass().getResource("/frontend/Catalogue.fxml"));
                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    stage.getScene().setRoot(root);
                                    stage.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else { errorLabel.setText("Incorrect password");}
                } else {errorLabel.setText("Username not found");}
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

    }

    @FXML
    public void handleSearchEnter(ActionEvent event) {
        String text = searchField.getText();

        if (text != null && !text.isBlank()) {
            CatalogueController.pendingSearchText = text.trim();
            switchPage(event, "Catalogue.fxml");
        }
    }

    @FXML
    public void goToCatalogue(ActionEvent event) {
        switchPage(event, "Catalogue.fxml");
    }

    @FXML
    public void goToCurrentPromotions(ActionEvent event) {
        switchPage(event, "CurrentPromotions.fxml");
    }

    @FXML
    public void goToCheckout(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }

    @FXML public void goToCommercialRegister(ActionEvent event){switchPage(event, "CommercialRegister.fxml");}
    @FXML public void goToNonCommercialRegister(ActionEvent event){switchPage(event, "NonCommercialRegister.fxml");}

    @FXML
    public void goToForgetPassword(ActionEvent event) {
        switchPage(event, "ForgetPassword.fxml");
    }


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