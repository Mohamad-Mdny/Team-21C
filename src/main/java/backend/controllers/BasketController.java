package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import backend.models.Item;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class BasketController {

    private static final double VAT_RATE = 0.00;
    
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
    void initialize() {
        configureBasketTable();
        basketTable.setPlaceholder(new Label("Your basket is empty."));
        loadUserBasket();
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

    private void loadUserBasket() {
        ObservableList<BasketRow> rows = FXCollections.observableArrayList();

        if (Main.m == null || Main.m.getBasket() == null || Main.m.getBasket().isEmpty()) {
            basketTable.setItems(rows);
            updateSummaryLabels(0, 0.0);
            return;
        }

        Map<Integer, BasketAccumulator> groupedItems = new LinkedHashMap<>();

        for (Item item : Main.m.getBasket()) {
            if (item == null) {
                continue;
            }

            BasketAccumulator accumulator = groupedItems.get(item.getItemID());

            if (accumulator == null) {
                accumulator = new BasketAccumulator(item);
                groupedItems.put(item.getItemID(), accumulator);
            }

            accumulator.quantity++;
        }

        int totalItemCount = 0;
        double subtotal = 0.0;

        for (BasketAccumulator accumulator : groupedItems.values()) {
            Item item = accumulator.item;
            int quantity = accumulator.quantity;
            double lineSubtotal = item.getPackageCost() * quantity;

            totalItemCount += quantity;
            subtotal += lineSubtotal;

            rows.add(new BasketRow(
                    item.getDescription(),
                    item.getPackageType(),
                    item.getUnit(),
                    quantity,
                    money(item.getPackageCost()),
                    money(lineSubtotal)
            ));
        }

        basketTable.setItems(rows);
        updateSummaryLabels(totalItemCount, subtotal);
    }

    private void updateSummaryLabels(int itemCount, double subtotal) {
        double vat = subtotal * VAT_RATE;
        double total = subtotal + vat;

        if(Main.m.isSignedIn()&& checkMemberDiscount(Main.member.getEmailAddress())){
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
        String targetFxml = (Main.m.isSignedIn()) ? "CheckoutAccount.fxml" : "CheckoutGuest.fxml";
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
        switchPage(event, "CurrentPromotions.fxml");
    }

    @FXML
    public void goToCheckout(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }  // idek, just leave it atp i cba , im having to overhaul checkout/basket


    @FXML
    public void handleAccountButton(ActionEvent event) {
        if (Main.m.isSignedIn()) {
            switchPage(event, "AccountSettings.fxml");
        } else {
            switchPage(event, "Login.fxml");
        }
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



    //gathers the number of purchases made by the member that is logged in
    //if the number of purchases is divisible by 10 then it will return true else false
    private boolean checkMemberDiscount(String emailAddress){
        DatabaseManager database = new DatabaseManager();
        Connection connection= database.makeConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT totalPurchases FROM member WHERE emailAddress=?");
            statement.setString(1,emailAddress);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                if (resultSet.getInt("totalPurchases") % 10 == 0){
                    System.out.println("Discount Active");
                    return true;
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    //this finds the number of purchases made by the member
    //updates the table to increments total purchases by 1
    private void incrementMemberPurchases(String emailAddress){
        int totalPurchases=0;
        DatabaseManager database = new DatabaseManager();
        Connection connection= database.makeConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT totalPurchases FROM member WHERE emailAddress=?");
            statement.setString(1,emailAddress);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                totalPurchases=resultSet.getInt("totalPurchases");
            }
            PreparedStatement statementTwo = connection.prepareStatement("UPDATE member SET totalPurchases=? WHERE emailAddress=?");
            statementTwo.setInt(1,totalPurchases+1);
            statementTwo.setString(2,emailAddress);
            statementTwo.execute();


        }catch(SQLException e){
            e.printStackTrace();
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