package backend.controllers;

import backend.Main;
import backend.models.ItemCell;
import backend.models.Order;
import backend.models.Transaction;
import backend.prm.controller.PromotionController;
import backend.prm.frontend.PromotionBasketTracker;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
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
import java.util.*;
import java.time.LocalDateTime;
import static backend.Main.VAT_RATE;
import static backend.Main.main;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CheckoutGuestController {

    @FXML
    public TextField emailField;
    @FXML
    public TextField ExpiryDate;
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
    private Order order= new Order();
    private Transaction transaction = new Transaction();
    private double total;

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
        for (ItemCell itemCell : Main.m.getBasket()) {
            if (itemCell == null) continue;
            BasketAccumulator acc = grouped.get(itemCell.getItemID());
            if (acc == null) {
                acc = new BasketAccumulator(itemCell);
                grouped.put(itemCell.getItemID(), acc);
            }
            acc.quantity++;
        }
        int totalItemCount = 0;
        double subtotal = 0.0;
        for (BasketAccumulator acc : grouped.values()) {
            ItemCell itemCell = acc.itemCell;
            int qty = acc.quantity;
            double lineSubtotal = itemCell.getPackageCost() * qty;
            totalItemCount += qty;
            subtotal += lineSubtotal;
            rows.add(new BasketRow(itemCell.getDescriptions(), itemCell.getPackageType(), itemCell.getUnit(), qty, money(itemCell.getPackageCost()), money(lineSubtotal)));
        }
        basketTable.setItems(rows);
        updateSummaryLabels(totalItemCount, subtotal);
    }

    private void updateSummaryLabels(int itemCount, double subtotal) {
        double vat = subtotal * VAT_RATE;
        total = subtotal + vat;
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
        String cardNumberRaw = safe(cardNumberField.getText()).replaceAll("\\s", "");
        String cvvRaw = safe(cvvField.getText());
        String expDate = safe(ExpiryDate.getText());
        String email = safe(emailField.getText());
        LocalDateTime timestamp = LocalDateTime.now();
        String time = timestamp.toString();
        String notes = safe(orderNotesArea.getText());

        // checks
        if (deliveryAddress.isBlank()) {
            purchaseStatusLabel.setText("Please enter a delivery address.");
            return;
        }
        if (billingAddress.isBlank()) {
            purchaseStatusLabel.setText("Please enter a billing address.");
            return;
        }
        String cardDigits = cardNumberRaw.replaceAll("\\s+", "");
        if (!safeCardNumber(cardDigits)) {
            return;
        }
        if (!cvvRaw.matches("\\d{3,4}")) {
            purchaseStatusLabel.setText("CVV must be 3 or 4 digits.");
            return;
        }
        if (expDate.isBlank()) {
            purchaseStatusLabel.setText("Please enter a expiry date.");
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            YearMonth expiry = YearMonth.parse(expDate, formatter);
            YearMonth now = YearMonth.now();

            if (expiry.isBefore(now)){
                purchaseStatusLabel.setText("Invalid expiry date.");
                return;
            }
        } catch (DateTimeParseException e) {
            purchaseStatusLabel.setText("Invalid expiry date.");
            return;
        }




        if (email.isBlank()) {
            purchaseStatusLabel.setText("Please enter an email address.");
            return;
        }
        String last4 = cardDigits.substring(cardDigits.length() - 4); // i fixed that
        String paymentMethod = "Card ending in " + last4;
        String deliveryOption = "Standard Delivery";
        String OrderID = Order.newUUID();


        List<ItemCell> items = Main.m.getBasket();

        boolean success = Main.m.purchase(OrderID, email, deliveryAddress, paymentMethod, deliveryOption, notes);
        if (success) {
            Order.saveOrderWithItems(OrderID, deliveryAddress, deliveryOption, email, items);

            Main.m.clearBasket();


            PromotionRepository repository = new PromotionRepository();
            PromotionService service = new PromotionService(repository);
            PromotionController promotionController = new PromotionController(service);

            String orderReference = "GST-" + System.currentTimeMillis();

            for (PromotionBasketTracker.Entry entry : PromotionBasketTracker.getEntries()) {
                promotionController.confirmOrderPayment(
                        entry.getCampaignId(),
                        entry.getItemId(),
                        entry.getQuantity(),
                        orderReference
                );
            }

            PromotionBasketTracker.clear();
            purchaseStatusLabel.setText("Purchase completed successfully.");


            transaction.saveTransaction(total,billingAddress, cardDigits, cvvRaw, time,email );

            loadBasket();
            switchPage(event, "Catalogue.fxml");
        } else {
            purchaseStatusLabel.setText("Purchase failed. Please check your basket and details.");
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean safeCardNumber(String cardNumber) {
        if (!cardNumber.matches("\\d{12,19}")) {
            purchaseStatusLabel.setText("Card number must be 12–19 digits (numbers only).");
            return false;
        }
        if (cardNumber == null || cardNumber.isEmpty()) {
            purchaseStatusLabel.setText("Card number must be 12–19 digits (numbers only).");

            return false;
        } else if (cardNumber.length() == 10) {

        }

        return true;
    }


    // switch page
    @FXML public void handleSearchEnter(ActionEvent event) {
        String text = safe(searchField.getText());
        if (!text.isBlank()) {
            CatalogueController.pendingSearchText = text;
            switchPage(event, "Catalogue.fxml");
        }
    }
    @FXML public void goToCatalogue(ActionEvent event) {
        switchPage(event, "Catalogue.fxml");
    }
    @FXML public void goToCurrentPromotions(ActionEvent event) {
        switchPage(event, "PromotionsPage.fxml");
    }
    @FXML public void goToBasket(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }
    @FXML public void handleAccountButton(ActionEvent event) {
        switchPage(event, "Login.fxml");
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

    private void updateAccountButtonText() {
        if (accountButton == null) return;
        switch (Main.userType()) {
            case "NonCommercial" : {
                accountButton.setText("Account Settings");break;
            }
            case "Admin" : {
                accountButton.setText("Dashboard");break;
            }
            default: {accountButton.setText("Sign In");break;}
        }
        // like yeah, itll always be Sign in, but yk, im too lazy 2 change it
    }

    private class BasketAccumulator {
        private final ItemCell itemCell;
        private int quantity;

        private BasketAccumulator(ItemCell itemCell) {
            this.itemCell = itemCell;
            this.quantity = 0;
        }
    }

    public class BasketRow {
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