package backend.controllers;

import backend.Main;
import backend.models.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AccountSettingsController {

    @FXML public Button History;
    @FXML private TextField searchField;
    @FXML private Button accountSettingsButton;
    @FXML private TextField emailField;
    @FXML private Button typeBox;
    @FXML private TextField validityField;
    @FXML private TextField DeliveryAddress;
    @FXML private TextField CardNumber;
    @FXML private TextField CVV;
    @FXML public TextArea PhoneNumber;

    @FXML private TextArea BillingAddress;

    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML private Label statusLabel;

    private Member member;
    private Snapshot beforeEditSnapshot;   // restore data
    private boolean editing = false;


    // restore data store
    private record Snapshot(
            String email,
            String validity,
            String DeliveryAddress,
            String CardNumber,
            String CVV,
            String BillingAddress,
            String PhoneNumber
    ) {}

    @FXML
    private void initialize() {

        this.member = Main.member;

        refreshFieldsFromMember();

        setEditing(false);

        statusLabel.setText("Signed in as: " + safe(member.getEmailAddress()));
        updateTypeBox();
    }

    @FXML
    private void updateTypeBox() {
        if (Objects.equals(member.getType(), "commercial")) {
            typeBox.setText("Commercial");
        } else {
            typeBox.setText("Non Commercial");
        }
    }


    @FXML
    public void onEdit(ActionEvent event) {
        beforeEditSnapshot = snapshotCurrentFields();
        setEditing(true);
    }

    @FXML
    public void onCancel(ActionEvent event) {
        if (!editing) return;

        restoreSnapshot(beforeEditSnapshot);
        setEditing(false);
    }

    @FXML
    public void onSave(ActionEvent event) {


        String email = emailField.getText();
        String deliveryAddress = DeliveryAddress.getText();
        String cardNumber = CardNumber.getText();
        String billingAddress = BillingAddress.getText();
        int cvv = Integer.parseInt(CVV.getText());
        String phoneNumber = PhoneNumber.getText();

        if (email == null || email.isBlank()) {
            return;
        }

        member.setEmailAddress(email);
        member.setDeliveryAddress(deliveryAddress);
        member.setCardNumber(cardNumber);
        member.setBillingAddress(billingAddress);
        member.setCVV(cvv);
        member.setPhoneNumber(phoneNumber);


        setEditing(false);
    }

    private void setEditing(boolean enable) {
        editing = enable;

        typeBox.setDisable(!enable);

        validityField.setEditable(false);

        DeliveryAddress.setEditable(enable);
        CardNumber.setEditable(enable);
        CVV.setEditable(enable);
        BillingAddress.setEditable(enable);


        emailField.setEditable(false);
        PhoneNumber.setEditable(enable);

        editButton.setDisable(enable);
        saveButton.setDisable(!enable);
        cancelButton.setDisable(!enable);
    }

    private void refreshFieldsFromMember() {
        emailField.setText(safe(member.getEmailAddress()));
        validityField.setText(safe(member.getValidityStatus()));
        DeliveryAddress.setText(safe(member.getDeliveryAddress()));
        CardNumber.setText((member.getCardNumber()));
        BillingAddress.setText(safe(member.getBillingAddress()));
        CVV.setText((Integer.toString(member.getCVV())));
        PhoneNumber.setText(member.getPhoneNumber());

    }

    private Snapshot snapshotCurrentFields() {
        return new Snapshot(
                emailField.getText(),
                validityField.getText(),
                DeliveryAddress.getText(),
                CardNumber.getText(),
                CVV.getText(),
                BillingAddress.getText(),
                PhoneNumber.getText()
        );
    }

    private void restoreSnapshot(Snapshot s) {
        if (s == null) return;

        emailField.setText(s.email());
        validityField.setText(s.validity());

        DeliveryAddress.setText(s.DeliveryAddress());
        CardNumber.setText(s.CardNumber());
        CVV.setText(s.CVV());
        BillingAddress.setText(s.BillingAddress());
        PhoneNumber.setText(s.PhoneNumber());
    }




    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
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
    public void signOut(ActionEvent event) {
        Main.m.signOut();
        switchPage(event, "Login.fxml");
    }

    @FXML public void goToCatalogue(ActionEvent event) {
        switchPage(event, "Catalogue.fxml");
    }
    @FXML public void goToCurrentPromotions(ActionEvent event) {
        switchPage(event, "CurrentPromotions.fxml");
    }
    @FXML public void goToCheckout(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }
    @FXML public void goToHistory(ActionEvent event) {
        switchPage(event, "AccountHistory.fxml");
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