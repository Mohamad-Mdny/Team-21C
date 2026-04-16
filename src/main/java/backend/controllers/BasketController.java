package backend.controllers;

import backend.Main;
import backend.models.ItemCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static backend.Main.VAT_RATE;


public class BasketController {

    @FXML
    public Button accountButton;

    @FXML
    private TextField searchField;

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


    @FXML
    void initialize() {
        configureBasketTable();
        basketTable.setPlaceholder(new Label("Your basket is empty."));
        loadUserBasket();
        updateAccountButtonText();
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

    private void loadUserBasket() {
        ObservableList<BasketRow> rows = FXCollections.observableArrayList();

        if (Main.m == null || Main.m.getBasket() == null || Main.m.getBasket().isEmpty()) {
            basketTable.setItems(rows);
            updateSummaryLabels(0, 0.0);
            return;
        }

        Map<String, BasketAccumulator> groupedItems = new LinkedHashMap<>();

        for (ItemCell itemCell : Main.m.getBasket()) {
            if (itemCell == null) {
                continue;
            }

            BasketAccumulator accumulator = groupedItems.get(Integer.toString(itemCell.getItemID()));

            if (accumulator == null) {
                accumulator = new BasketAccumulator(itemCell);
                groupedItems.put(Integer.toString(itemCell.getItemID()), accumulator);
            }

            accumulator.quantity++;
        }

        int totalItemCount = 0;
        double subtotal = 0.0;

        for (BasketAccumulator accumulator : groupedItems.values()) {
            ItemCell itemCell = accumulator.itemCell;
            int quantity = accumulator.quantity;
            double lineSubtotal = itemCell.getPackageCost() * quantity;

            totalItemCount += quantity;
            subtotal += lineSubtotal;

            rows.add(new BasketRow(
                    itemCell.getDescriptions(),
                    itemCell.getPackageType(),
                    itemCell.getUnit(),
                    quantity,
                    money(itemCell.getPackageCost()),
                    money(lineSubtotal)
            ));
        }

        basketTable.setItems(rows);
        updateSummaryLabels(totalItemCount, subtotal);
    }

    private void updateSummaryLabels(int itemCount, double subtotal) {
        double vat = subtotal * VAT_RATE;
        double total = subtotal + vat;

        if(Main.userType().equals("NonCommercial") && Main.member.checkMemberDiscount(Main.member.getUserName())){
            total*=0.9;
        }



        itemCountLabel.setText("Items in basket: " + itemCount);

        subtotalSideLabel.setText("Subtotal: " + money(subtotal));
        vatSideLabel.setText("VAT: " + money(vat));
        totalSideLabel.setText("Total: " + money(total));

        subtotalBottomLabel.setText("Subtotal: " + money(subtotal));
        vatBottomLabel.setText("VAT: " + money(vat));
        totalBottomLabel.setText("Total: " + money(total));
    }

    private String money(double value) {
        return String.format("£%.2f", value);
    }

    @FXML
    public void clearBasket(ActionEvent event) {
        if (Main.m != null && Main.m.getBasket() != null) {
            Main.m.getBasket().clear();
        }
        loadUserBasket();
    }



    @FXML
    public void purchaseBasket(ActionEvent event) {
        String targetFxml = Main.userType().equals("NonCommercial")  ? "CheckoutAccount.fxml" : "CheckoutGuest.fxml";
        switchPage(event, targetFxml);
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
    public void goToCheckout(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }  // idek, just leave it atp i cba , im having to overhaul checkout/basket


    @FXML
    public void handleAccountButton(ActionEvent event) {
        switch (Main.userType()) {
            case "NonCommercial" : {switchPage(event, "AccountSettings.fxml");break;}
            case "Admin" : {switchPage(event, "AdminPage.fxml");break;}
            default: {switchPage(event, "Login.fxml");}
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
            default: {accountButton.setText("Sign In");}
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