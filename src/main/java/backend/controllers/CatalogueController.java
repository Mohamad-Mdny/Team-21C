package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import backend.models.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet; import java.sql.SQLException;
import java.sql.Statement; import java.util.Objects;

public class CatalogueController {
    @FXML private GridPane catalogueGrid;
    @FXML private Button accountButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> packageTypeFilter;
    @FXML private ComboBox<String> unitFilter;
    @FXML private TextField maxPriceField;
    @FXML private CheckBox inStockOnly;
    @FXML private CheckBox lowStockOnly;

    private final ObservableList<Item> masterData = FXCollections.observableArrayList();
    private FilteredList<Item> filteredData; private Integer selectedItemId = null;

    @FXML void initialize() {
        loadData();
        filteredData = new FilteredList<>(masterData, item -> true);
        configureFilters();
        updateAccountButton();
        if (CheckoutController.pendingSearchText != null && !CheckoutController.pendingSearchText.isBlank()) {
            searchField.setText(CheckoutController.pendingSearchText);
            CheckoutController.pendingSearchText = "";
        }
        updateFilters(); }
    private void loadData() {
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();
        if (connection == null) {
            System.out.println("Database connection failed.");
            return;
        }
        String sql = "SELECT * FROM Catalogue";
        try (
                connection;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                masterData.add(new Item( resultSet.getInt("ItemID"),
                        resultSet.getString("Description"),
                        resultSet.getString("PackageType"),
                        resultSet.getString("Unit"),
                        resultSet.getInt("UnitsInAPack"),
                        resultSet.getFloat("PackageCost"),
                        resultSet.getInt("Availability"),
                        resultSet.getInt("StockLimit") )
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureFilters() {
        packageTypeFilter.getItems().clear();

        packageTypeFilter.getItems().add("All");
        packageTypeFilter.getItems().addAll(
                masterData.stream().map(Item::getPackageType).filter(Objects::nonNull).distinct().sorted().toList());
        packageTypeFilter.setValue("All");
        unitFilter.getItems().clear();
        unitFilter.getItems().add("All");
        unitFilter.getItems().addAll( masterData.stream() .map(Item::getUnit) .filter(Objects::nonNull) .distinct() .sorted() .toList() );
        unitFilter.setValue("All");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        packageTypeFilter.valueProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        unitFilter.valueProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        maxPriceField.textProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        inStockOnly.selectedProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        lowStockOnly.selectedProperty().addListener((obs, oldValue, newValue) -> updateFilters());
    }


    private void updateFilters() {
        if (filteredData == null) { return;}
        filteredData.setPredicate(item -> {
            if (item == null) { return false; }
            String searchText = safeLower(searchField.getText());
            String selectedPackageType = packageTypeFilter.getValue();
            String selectedUnit = unitFilter.getValue();
            if (!searchText.isEmpty()) {
                boolean matchesSearch = String.valueOf(item.getItemID()).toLowerCase().contains(searchText) ||
                        safeLower(item.getDescription()).contains(searchText);
                if (!matchesSearch) { return false; }
            }
            if (selectedPackageType != null && !selectedPackageType.equals("All")) {
                if (!safeLower(item.getPackageType()).equals(selectedPackageType.toLowerCase())) { return false; }
            }
            if (selectedUnit != null && !selectedUnit.equals("All")) {
                if (!safeLower(item.getUnit()).equals(selectedUnit.toLowerCase())) { return false; }
            }
            if (inStockOnly.isSelected() && item.getAvailability() <= 0) { return false; }
            if (lowStockOnly.isSelected() && item.getAvailability() > item.getStockLimit()) { return false; }
            String maxPriceText = maxPriceField.getText() == null ? "" : maxPriceField.getText().trim();
            if (!maxPriceText.isEmpty()) {
                try {
                    float maxPrice = Float.parseFloat(maxPriceText);
                    if (item.getPackageCost() > maxPrice) { return false; }
                } catch (NumberFormatException ignored) { }
            } return true; }
        );
        refreshCatalogueGrid();
    }

    private void refreshCatalogueGrid() {
        catalogueGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        for (Item item : filteredData) {
            VBox itemCard = createItemCard(item);
            catalogueGrid.add(itemCard, column, row);
            column++;
            if (column == 5) {
                column = 0;
                row++; }
        }
    }

    private VBox createItemCard(Item item) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setPrefWidth(180); card.setMinWidth(180);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.TOP_LEFT);
        applyCardStyle(card, item);
        Label titleLabel = new Label(item.getDescription());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        Label itemIdLabel = new Label("Item ID: " + item.getItemID());
        Label packageTypeLabel = new Label("Package: " + item.getPackageType());
        Label unitLabel = new Label("Unit: " + item.getUnit());
        Label unitsInPackLabel = new Label("Units in pack: " + item.getUnitsInAPack());
        Label priceLabel = new Label(String.format("Price: £%.2f", item.getPackageCost()));
        Label availabilityLabel = new Label("Availability: " + item.getAvailability());
        Label stockLimitLabel = new Label("Stock limit: " + item.getStockLimit());
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setMaxWidth(Double.MAX_VALUE);
        addToCartButton.setOnAction(event -> addItemToCart(item));
        card.setOnMouseClicked(event -> { selectedItemId = item.getItemID();
            refreshCatalogueGrid();
        });
        card.getChildren().addAll( titleLabel, itemIdLabel, packageTypeLabel, unitLabel, unitsInPackLabel, priceLabel, availabilityLabel, stockLimitLabel, spacer, addToCartButton );
        return card;
    }

    private void applyCardStyle(VBox card, Item item) {
        boolean isSelected = selectedItemId != null && selectedItemId == item.getItemID();
        String baseStyle = "-fx-background-color: white; " + "-fx-border-color: #d1d5db; " + "-fx-border-radius: 8; " + "-fx-background-radius: 8; " + "-fx-cursor: hand;";
        String selectedStyle = "-fx-border-color: #2563eb; -fx-border-width: 2;";
        String normalStyle = "-fx-border-width: 1;";
        card.setStyle(baseStyle + (isSelected ? selectedStyle : normalStyle));
    }

    private void addItemToCart(Item item) {
        if (item == null) { return; }
        Main.m.addItem(item);
        System.out.println(item.getDescription() + " added to cart.");
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(); }

    @FXML public void clearFilters(ActionEvent event) {
        searchField.clear();
        packageTypeFilter.setValue("All");
        unitFilter.setValue("All");
        maxPriceField.clear();
        inStockOnly.setSelected(false);
        lowStockOnly.setSelected(false);
        updateFilters();
    }

    @FXML public void goToCatalogue(ActionEvent event) {
        switchPage(event, "Catalogue.fxml");
    }

    @FXML public void goToCurrentPromotions(ActionEvent event) {
        switchPage(event, "CurrentPromotions.fxml");
    }

    @FXML public void goToCheckout(ActionEvent event) {
        switchPage(event, "Checkout.fxml");
    }

    private void updateAccountButton() {
        if (Main.m != null && Main.m.isSignedIn()) {
            accountButton.setText("Account Settings");
        } else {
            accountButton.setText("Sign In");
        }
    }

    @FXML public void handleAccountButton(ActionEvent event) {
        if (Main.m != null && Main.m.isSignedIn()) {
            switchPage(event, "AccountSettings.fxml");
        } else {
            switchPage(event, "Logintest.fxml");
        }
    }
    private void switchPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load( Objects.requireNonNull(getClass().getResource("/frontend/" + fxmlFile)) );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.show(); }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}