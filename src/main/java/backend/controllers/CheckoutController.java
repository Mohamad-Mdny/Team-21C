package backend.controllers;

import backend.Main;
import backend.models.Item;
import java.util.LinkedHashMap;
import java.util.Map;import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CheckoutController {

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

    public static String pendingSearchText = "";

    @FXML
    void initialize() {
        configureBasketTable();
        loadUserBasket();
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

        for (BasketAccumulator accumulator : groupedItems.values()) {
            Item item = accumulator.item;
            int quantity = accumulator.quantity;
            float subtotal = item.getPackageCost() * quantity;

            rows.add(new BasketRow(
                    item.getDescription(),
                    item.getPackageType(),
                    item.getUnit(),
                    quantity,
                    String.format("£%.2f", item.getPackageCost()),
                    String.format("£%.2f", subtotal)
            ));
        }

        basketTable.setItems(rows);
    }

    @FXML
    public void handleSearchEnter(ActionEvent event) {
        String text = searchField.getText();

        if (text != null && !text.isBlank()) {
            pendingSearchText = text.trim();
            switchPage(event, "Catalogue.fxml");
        }
    }
    @FXML
    public void clearBasket(ActionEvent event) {
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
        switchPage(event, "Checkout.fxml");
    }

    @FXML
    public void goToAccountSettings(ActionEvent event) {
        switchPage(event, "AccountSettings.fxml");
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
    private static class BasketAccumulator {
        private final Item item;
        private int quantity;

        private BasketAccumulator(Item item) {
            this.item = item;
            this.quantity = 0;
        }
    }
}