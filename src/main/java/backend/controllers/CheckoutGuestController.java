package backend.controllers;

import backend.Main;
import backend.models.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CheckoutGuestController {
    private static final double VAT_RATE = 0.00;

    @FXML
    public TextField emailField;
    @FXML
    private TextField searchField;
    @FXML
    private Button accountButton;

    @FXML
    private TableView<BasketRow> basketTable;
    @FXML
    private TableColumn<BasketRow, String> itemColumn;
    @FXML
    private TableColumn<BasketRow, String> packageColumn;
    @FXML
    private TableColumn<BasketRow, String> unitColumn;
    @FXML
    private TableColumn<BasketRow, Integer> quantityColumn;
    @FXML
    private TableColumn<BasketRow, String> priceColumn;
    @FXML
    private TableColumn<BasketRow, String> subtotalColumn;
    @FXML
    private Label itemCountLabel;
    @FXML
    private Label subtotalSideLabel;
    @FXML
    private Label vatSideLabel;
    @FXML
    private Label totalSideLabel;
    @FXML
    private Label subtotalBottomLabel;
    @FXML
    private Label vatBottomLabel;
    @FXML
    private Label totalBottomLabel;
    @FXML
    private TextArea orderNotesArea;
    @FXML
    private TextArea deliveryAddressArea;
    @FXML
    private TextArea billingAddressArea;
    @FXML
    private TextField cardNumberField;
    @FXML
    private PasswordField cvvField;
    @FXML
    private Label purchaseStatusLabel;

    @FXML
    void initialize() {
        configureBasketTable();
        basketTable.setPlaceholder(new Label("Your basket is empty."));
        loadBasket();
        updateAccountButtonText();
    }

    private void configureBasketTable() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
        packageColumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void loadBasket() {
        ObservableList<BasketRow> rows = FXCollections.observableArrayList();
        if (Main.m == null || Main.m.getBasket() == null || Main.m.getBasket().isEmpty()) {
            basketTable.setItems(rows);
            updateSummaryLabels(0, 0.0);
            return;
        }
        Map<Integer, BasketAccumulator> grouped = new LinkedHashMap<>();
        for (Item item : Main.m.getBasket()) {
            if (item == null) continue;
            BasketAccumulator acc = grouped.get(item.getItemID());
            if (acc == null) {
                acc = new BasketAccumulator(item);
                grouped.put(item.getItemID(), acc);
            }
            acc.quantity++;
        }
        int totalItemCount = 0;
        double subtotal = 0.0;
        for (BasketAccumulator acc : grouped.values()) {
            Item item = acc.item;
            int qty = acc.quantity;
            double lineSubtotal = item.getPackageCost() * qty;
            totalItemCount += qty;
            subtotal += lineSubtotal;
            rows.add(new BasketRow(item.getDescription(), item.getPackageType(), item.getUnit(), qty, money(item.getPackageCost()), money(lineSubtotal)));
        }
        basketTable.setItems(rows);
        updateSummaryLabels(totalItemCount, subtotal);
    }

    private void updateSummaryLabels(int itemCount, double subtotal) {
        double vat = subtotal * VAT_RATE;
        double total = subtotal + vat;
        itemCountLabel.setText("Items in basket: " + itemCount);
        subtotalSideLabel.setText("Subtotal: " + money(subtotal));
        vatSideLabel.setText("VAT: " + money(vat));
        totalSideLabel.setText("Total: " + money(total));
        subtotalBottomLabel.setText("Subtotal: " + money(subtotal));
        vatBottomLabel.setText("VAT: " + money(vat));
        totalBottomLabel.setText("Total: " + money(total));
    }

    private String money(double v) {
        return String.format("£%.2f", v);
    }

    @FXML
    public void purchaseGuest(ActionEvent event) {
        purchaseStatusLabel.setText("");
        if (Main.m == null) {
            purchaseStatusLabel.setText("No basket/user session is loaded.");
            return;
        }
        String deliveryAddress = safe(deliveryAddressArea.getText());
        String billingAddress = safe(billingAddressArea.getText());
        String cardNumberRaw = safe(cardNumberField.getText());
        String cvvRaw = safe(cvvField.getText());
        String email = safe(emailField.getText());

        String notes = safe(orderNotesArea.getText());
        if (deliveryAddress.isBlank()) {
            purchaseStatusLabel.setText("Please enter a delivery address.");
            return;
        }
        if (billingAddress.isBlank()) {
            purchaseStatusLabel.setText("Please enter a billing address.");
            return;
        }
        String cardDigits = cardNumberRaw.replaceAll("\\s+", "");
        if (!cardDigits.matches("\\d{12,19}")) {
            purchaseStatusLabel.setText("Card number must be 12–19 digits (numbers only).");
            return;
        }
        if (!cvvRaw.matches("\\d{3,4}")) {
            purchaseStatusLabel.setText("CVV must be 3 or 4 digits.");
            return;
        }
        if (email.isBlank()) {
            purchaseStatusLabel.setText("Please enter an email address.");
            return;
        }
        String last4 = cardDigits.substring(cardDigits.length() - 4);
        String paymentMethod = "Card ending in " + last4;
        String deliveryOption = "Standard Delivery";
        boolean success = Main.m.purchase(email, deliveryAddress, paymentMethod, deliveryOption, notes);
        if (success) {
            purchaseStatusLabel.setText("Purchase completed successfully.");
            loadBasket();
        } else {
            purchaseStatusLabel.setText("Purchase failed. Please check your basket and details.");
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    @FXML
    public void handleSearchEnter(ActionEvent event) {
        String text = safe(searchField.getText());
        if (!text.isBlank()) {
            CatalogueController.pendingSearchText = text;
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
    public void goToBasket(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }

    @FXML
    public void handleAccountButton(ActionEvent event) {
        switchPage(event, "Login.fxml");
    }

    private void updateAccountButtonText() {
        if (accountButton == null) return;
        if (Main.m != null && Main.m.isSignedIn()) {
            accountButton.setText("Account Settings");
        } else {
            accountButton.setText("Sign In");
        }
    }

    private void switchPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/frontend/" + fxmlFile)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (purchaseStatusLabel != null) {
                purchaseStatusLabel.setText("Navigation failed: " + e.getMessage());
            }
        }
    }

    private static class BasketAccumulator {
        private final Item item;
        private int quantity;

        private BasketAccumulator(Item item) {
            this.item = item;
            this.quantity = 0;
        }
    }

    public static class BasketRow {
        private final String item;
        private final String packageType;
        private final String unit;
        private final Integer quantity;
        private final String unitPrice;
        private final String subtotal;

        public BasketRow(String item, String packageType, String unit, Integer quantity, String unitPrice, String subtotal) {
            this.item = item;
            this.packageType = packageType;
            this.unit = unit;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public String getItem() {
            return item;
        }

        public String getPackageType() {
            return packageType;
        }

        public String getUnit() {
            return unit;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public String getUnitPrice() {
            return unitPrice;
        }

        public String getSubtotal() {
            return subtotal;
        }
    }
}