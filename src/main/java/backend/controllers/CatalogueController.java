package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import backend.models.ItemCell;
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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class CatalogueController {
    public ScrollPane catalogueScrollPane;
    @FXML private GridPane catalogueGrid;
    @FXML private Button accountButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> packageTypeFilter;
    @FXML private ComboBox<String> unitFilter;
    @FXML private TextField maxPriceField;
    @FXML private CheckBox inStockOnly;
    @FXML private CheckBox lowStockOnly;

    public static String pendingSearchText = "";


    @FXML
    private Label memberSession;

    private final ObservableList<ItemCell> masterData = FXCollections.observableArrayList();
    private FilteredList<ItemCell> filteredData; private String selectedItemId = null;

    @FXML void initialize() {
        if(Main.member !=null) {
            if (Main.userType().equals("NonCommercial") )
                memberSession.setText(Main.member.getUserName());
        }
        loadData();
        filteredData = new FilteredList<>(masterData, item -> true);
        configureFilters();
        updateAccountButton();
        if (pendingSearchText != null && !pendingSearchText.isBlank()) {
            searchField.setText(pendingSearchText);
            pendingSearchText = "";
        }
        updateFilters(); }
    private void loadData() {
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();
        if (connection == null) {
            return;
        }

        String sql = "SELECT * FROM Catalogue";
        try (
                connection;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                masterData.add(new ItemCell( resultSet.getInt("ItemID"),
                        resultSet.getString("Descriptions"),
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
                masterData.stream().map(ItemCell::getPackageType).filter(Objects::nonNull).distinct().sorted().toList());
        packageTypeFilter.setValue("All");
        unitFilter.getItems().clear();
        unitFilter.getItems().add("All");
        unitFilter.getItems().addAll( masterData.stream() .map(ItemCell::getUnit) .filter(Objects::nonNull) .distinct() .sorted() .toList() );
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
                        safeLower(item.getDescriptions()).contains(searchText);
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
        for (ItemCell itemCell : filteredData) {
            VBox itemCard = createItemCard(itemCell);
            catalogueGrid.add(itemCard, column, row);
            column++;
            if (column == 5) {
                column = 0;
                row++; }
        }
    }

    private VBox createItemCard(ItemCell itemCell) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setPrefWidth(180); card.setMinWidth(180);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.TOP_LEFT);
        applyCardStyle(card, itemCell);
        Label titleLabel = new Label(itemCell.getDescriptions());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        Label itemIdLabel = new Label("Item ID: " + itemCell.getItemID());
        Label packageTypeLabel = new Label("Package: " + itemCell.getPackageType());
        Label unitLabel = new Label("Unit: " + itemCell.getUnit());
        Label unitsInPackLabel = new Label("Units in pack: " + itemCell.getUnitsInAPack());
        Label priceLabel = new Label(String.format("Price: £%.2f", itemCell.getPackageCost()));
        Label availabilityLabel = new Label("Availability: " + itemCell.getAvailability());
//        Label stockLimitLabel = new Label("Stock limit: " + item.getStockLimit());
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setMaxWidth(Double.MAX_VALUE);
        addToCartButton.setOnAction(event -> addItemToCart(itemCell));
        card.setOnMouseClicked(event -> { selectedItemId = itemCell.getItemID();
            refreshCatalogueGrid();
        });
        card.getChildren().addAll( titleLabel, itemIdLabel, packageTypeLabel, unitLabel, unitsInPackLabel, priceLabel, availabilityLabel, spacer, addToCartButton );
        return card;
    }

    private void applyCardStyle(VBox card, ItemCell itemCell) {
        boolean isSelected = selectedItemId != null && selectedItemId == itemCell.getItemID();
        String baseStyle = "-fx-background-color: white; " + "-fx-border-color: #d1d5db; " + "-fx-border-radius: 8; " + "-fx-background-radius: 8; " + "-fx-cursor: hand;";
        String selectedStyle = "-fx-border-color: #2563eb; -fx-border-width: 2;";
        String normalStyle = "-fx-border-width: 1;";
        card.setStyle(baseStyle + (isSelected ? selectedStyle : normalStyle));
    }

    private void addItemToCart(ItemCell itemCell) {
        if (itemCell == null) { return; }
        Main.m.addItem(itemCell);
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
    //@FXML public void goToCurrentPromotions(ActionEvent event) {switchPage(event, "PromotionsPage.fxml");}


    @FXML public void goToCurrentPromotions(ActionEvent event) {switchPage(event, "PromotionsPage.fxml");}


    @FXML public void goToCheckout(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }
    @FXML public void goToCommercialRegister(ActionEvent event){switchPage(event, "CommercialRegister.fxml");}
    @FXML public void goToNonCommercialRegister(ActionEvent event){switchPage(event, "NonCommercialRegister.fxml");}

    private void updateAccountButton() {
        switch (Main.userType()) {
            case "NonCommercial" : {
                accountButton.setText("Account Settings");break;
            }
            case "Admin" : {
                accountButton.setText("Dashboard");break;
            }
            default: {accountButton.setText("Sign In");break;}
        }
    }

    @FXML
    public void handleAccountButton(ActionEvent event) {
        switch (Main.userType()) {
            case "NonCommercial" : {switchPage(event, "AccountSettings.fxml"); break;}
            case "Admin" : {switchPage(event, "AdminPage.fxml");break;}
            default: {switchPage(event, "Login.fxml");break;}
        }
    }
    private void switchPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load( Objects.requireNonNull(getClass().getResource("/frontend/" + fxmlFile)) );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



}