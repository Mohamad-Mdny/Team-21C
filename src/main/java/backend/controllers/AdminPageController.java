package backend.controllers;

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
import java.util.Objects;

import static backend.Main.VAT_RATE;

public class AdminPageController {

    @FXML private TextField vatField;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        vatField.setText(String.valueOf(VAT_RATE));
    }

    @FXML
    public void goToAdminDashboard(ActionEvent event) {
        switchPage(event, "AdminDashboard.fxml");
    }

    @FXML
    public void logout(ActionEvent event) {
        // Basic logout
        if (Main.m != null) Main.m.signOut();
        Main.admin = null;
        Main.member = null;

        switchPage(event, "Login.fxml");
    }

    @FXML
    public void saveVat(ActionEvent event) {
        String text = vatField.getText();

        try {
            double vat = Double.parseDouble(text.trim());

            if (vat < 0 || vat > 100) {
                statusLabel.setText("VAT must be between 0 and 100.");
                return;
            }

            VAT_RATE = (vat);
            statusLabel.setText("VAT saved. Current VAT: " + vat + "%");

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid VAT value. Enter a number like 0, 5, or 20.");
        }
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
            if (statusLabel != null) statusLabel.setText("Navigation failed: " + e.getMessage());
        }
    }
}