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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.LocalDateTime;
import static backend.Main.VAT_RATE;
import static backend.Main.member;


public class CheckoutAccountController {

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
    private ComboBox<String> deliveryAddressBox;
    @FXML
    private ComboBox<String> paymentMethodBox;
    @FXML
    private ComboBox<String> deliveryOptionBox;
    @FXML
    private TextArea orderNotesArea;
    @FXML
    private Label purchaseStatusLabel;
    @FXML
    private Label discountStatus;
    private Order order= new Order();
    private Transaction transaction = new Transaction();
    private double total;

    @FXML
    void initialize() {
        configureBasketTable();
        basketTable.setPlaceholder(new Label("Your basket is empty."));
        updateAccountButtonText();
        if (Main.m == null || Main.userType().equals("User") ) { // idek what kind of safe checking this is
            purchaseStatusLabel.setText("You are not signed in. Please use Guest Checkout or Sign In.");
            switchPage(new ActionEvent(accountButton, null), "Login.fxml");
        }
        configureCheckoutOptions();
        loadBasket();
        if(Main.member != null){
            if (Main.member.checkMemberDiscount(Main.member.getUserName())){
                discountStatus.setText("Discount Status: Active");
            }
            else {
                discountStatus.setText("Discount Status: Inactive");
            }
        }
    }

    private void configureBasketTable() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
        packageColumn.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void configureCheckoutOptions() {
        deliveryOptionBox.getItems().setAll("Standard Delivery", "Next Day Delivery", "Click & Collect");
        deliveryOptionBox.setValue("Standard Delivery");

        if (Main.m != null && Main.userType().equals("NonCommercial") ) {
            try {
                deliveryAddressBox.getItems().setAll(Main.member.getDeliveryAddress());
                deliveryAddressBox.setValue(Main.member.getDeliveryAddress());
                String maskedCard = "Card ending in " + Main.member.getCardNumber();
                paymentMethodBox.getItems().setAll(maskedCard);
                paymentMethodBox.setValue(maskedCard);
            } catch (Exception e) {
                purchaseStatusLabel.setText("Could not load saved account details. Please check Account Settings.");
            }
        }
    }

    private void loadBasket() {
        ObservableList<BasketRow> rows = FXCollections.observableArrayList();
        if (Main.m == null || Main.m.getBasket() == null || Main.m.getBasket().isEmpty()) {
            basketTable.setItems(rows);
            updateSummaryLabels(0, 0.0);
            return;
        }
        Map<String, BasketAccumulator> grouped = new LinkedHashMap<>();
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
        if(Main.member.checkMemberDiscount(Main.member.getUserName())){
            total *= 0.9;
        }

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
    public void purchaseAccount(ActionEvent event) {

        String address = deliveryAddressBox.getValue();
        String paymentMethod = paymentMethodBox.getValue();
        String deliveryOption = deliveryOptionBox.getValue();
        String notes = orderNotesArea.getText();
        LocalDateTime timestamp = LocalDateTime.now();
        String time = timestamp.toString();

        // safe
        if (address == null || address.isBlank()) {
            purchaseStatusLabel.setText("Please select a delivery address.");
            return;
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            purchaseStatusLabel.setText("Please select a payment method.");
            return;
        }
        if (deliveryOption == null || deliveryOption.isBlank()) {
            purchaseStatusLabel.setText("Please select a delivery option.");
            return;
        }
        String OrderID = Order.newUUID();

        List<ItemCell> items = Main.member.getBasket();
        boolean success = Main.m.purchase(OrderID, member.getUserName(), member.getDeliveryAddress(), paymentMethod, deliveryOption, notes);
        if (success) {
            Order.saveOrderWithItems(OrderID, member.getDeliveryAddress(), deliveryOption, member.getUserName(), items);
            Main.member.clearBasket();
            Main.member.incrementMemberPurchases();

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


            transaction.saveTransaction(total, member.getBillingAddress(), member.getCardNumber(), Integer.toString(member.getCVV()), time,member.getUserName());

            loadBasket();
            switchPage(event, "Catalogue.fxml");
        } else {
            purchaseStatusLabel.setText("Purchase failed. Please check your basket and details.");
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
        switchPage(event, "PromotionsPage.fxml");
    }

    @FXML
    public void goToBasket(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }

    @FXML
    public void handleAccountButton(ActionEvent event) {
        switch (Main.userType()) {
            case "NonCommercial" : {switchPage(event, "AccountSettings.fxml"); break;}
            case "Admin" : {switchPage(event, "AdminPage.fxml");break;}
            default: {switchPage(event, "Login.fxml");break;}
        }
    }

    private void updateAccountButtonText() {
        if (accountButton == null) return;
        switch (Main.userType()) {
            case "NonCommercial" : {
                accountButton.setText("Account Settings"); break;
            }
            case "Admin" : {
                accountButton.setText("Dashboard");break;
            }
            default: {accountButton.setText("Sign In");break;
            }
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
        private final ItemCell itemCell;
        private int quantity;

        private BasketAccumulator(ItemCell itemCell) {
            this.itemCell = itemCell;
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