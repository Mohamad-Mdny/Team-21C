package backend.controllers;

import backend.DatabaseManager;
import backend.models.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CatalogueController {

    @FXML
    private TableView<Item> Catalogue;
    @FXML
    private TableColumn<Item, Integer> ItemID;
    @FXML
    private TableColumn<Item, String> Description;
    @FXML
    private TableColumn<Item, String> PackageType;
    @FXML
    private TableColumn<Item, String> Unit;
    @FXML
    private TableColumn<Item, Integer> UnitsInAPack;
    @FXML
    private TableColumn<Item, Float> PackageCost;
    @FXML
    private TableColumn<Item, Integer> Availability;
    @FXML
    private TableColumn<Item, Integer> StockLimit;

    @FXML
    private Button sortItemIDButton;
    @FXML
    private Button sortDescriptionButton;
    @FXML
    private Button sortPackageTypeButton;
    @FXML
    private Button sortUnitButton;
    @FXML
    private Button sortUnitsInAPackButton;
    @FXML
    private Button sortPackageCostButton;
    @FXML
    private Button sortAvailabilityButton;
    @FXML
    private Button sortStockLimitButton;

    @FXML
    private TextField quantity;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> packageTypeFilter;
    @FXML
    private ComboBox<String> unitFilter;
    @FXML
    private TextField maxPriceField;
    @FXML
    private CheckBox inStockOnly;
    @FXML
    private CheckBox lowStockOnly;

    private final HashMap<Item, Integer> checkout = new HashMap<>();
    private final ObservableList<Item> masterData = FXCollections.observableArrayList();
    private FilteredList<Item> filteredData;

    private final Map<Button, TableColumn<Item, ?>> sortButtonToColumn = new LinkedHashMap<>();
    private final Map<Button, String> sortButtonLabels = new LinkedHashMap<>();
    private final Map<Button, SortState> sortStates = new LinkedHashMap<>();

    private enum SortState {
        INACTIVE,
        ASCENDING,
        DESCENDING
    }

    @FXML
    void initialize() {
        configureTable();
        loadData();

        filteredData = new FilteredList<>(masterData, item -> true);
        SortedList<Item> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(Catalogue.comparatorProperty());
        Catalogue.setItems(sortedData);

        configureFilters();
        configureSortButtons();

        Catalogue.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTable() {
        ItemID.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        Description.setCellValueFactory(new PropertyValueFactory<>("description"));
        PackageType.setCellValueFactory(new PropertyValueFactory<>("packageType"));
        Unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        UnitsInAPack.setCellValueFactory(new PropertyValueFactory<>("unitsInAPack"));
        PackageCost.setCellValueFactory(new PropertyValueFactory<>("packageCost"));
        Availability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        StockLimit.setCellValueFactory(new PropertyValueFactory<>("stockLimit"));
    }

    private void loadData() {
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();

        if (connection == null) {
            System.out.println("Database connection failed.");
            return;
        }

        String sql = "SELECT * FROM Catalogue";

        try (connection;
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                masterData.add(new Item(
                        resultSet.getInt("ItemID"),
                        resultSet.getString("Description"),
                        resultSet.getString("PackageType"),
                        resultSet.getString("Unit"),
                        resultSet.getInt("UnitsInAPack"),
                        resultSet.getFloat("PackageCost"),
                        resultSet.getInt("Availability"),
                        resultSet.getInt("StockLimit")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureFilters() {


        packageTypeFilter.getItems().clear();
        packageTypeFilter.getItems().add("All");
        packageTypeFilter.getItems().addAll(
                masterData.stream()
                        .map(Item::getPackageType)
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted()
                        .toList()
        );
        packageTypeFilter.setValue("All");

        unitFilter.getItems().clear();
        unitFilter.getItems().add("All");
        unitFilter.getItems().addAll(
                masterData.stream()
                        .map(Item::getUnit)
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted()
                        .toList()
        );
        unitFilter.setValue("All");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        packageTypeFilter.valueProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        unitFilter.valueProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        maxPriceField.textProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        inStockOnly.selectedProperty().addListener((obs, oldValue, newValue) -> updateFilters());
        lowStockOnly.selectedProperty().addListener((obs, oldValue, newValue) -> updateFilters());
    }

    private void updateFilters() {
        if (filteredData == null) {
            return;
        }

        filteredData.setPredicate(item -> {
            if (item == null) {
                return false;
            }

            String searchText = safeLower(searchField.getText());
            String selectedPackageType = packageTypeFilter.getValue();
            String selectedUnit = unitFilter.getValue();

            if (!searchText.isEmpty()) {
                boolean matchesSearch =
                        String.valueOf(item.getItemID()).toLowerCase().contains(searchText) ||
                                safeLower(item.getDescription()).contains(searchText) ||
                                safeLower(item.getPackageType()).contains(searchText) ||
                                safeLower(item.getUnit()).contains(searchText);

                if (!matchesSearch) {
                    return false;
                }
            }


            if (selectedPackageType != null && !selectedPackageType.equals("All")) {
                if (!safeLower(item.getPackageType()).equals(selectedPackageType.toLowerCase())) {
                    return false;
                }
            }

            if (selectedUnit != null && !selectedUnit.equals("All")) {
                if (!safeLower(item.getUnit()).equals(selectedUnit.toLowerCase())) {
                    return false;
                }
            }

            if (inStockOnly.isSelected() && item.getAvailability() <= 0) {
                return false;
            }

            if (lowStockOnly.isSelected() && item.getAvailability() > item.getStockLimit()) {
                return false;
            }

            String maxPriceText = maxPriceField.getText() == null ? "" : maxPriceField.getText().trim();
            if (!maxPriceText.isEmpty()) {
                try {
                    float maxPrice = Float.parseFloat(maxPriceText);
                    if (item.getPackageCost() > maxPrice) {
                        return false;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            return true;
        });
    }

    private void configureSortButtons() {
        registerSortButton(sortItemIDButton, ItemID, "Item ID");
        registerSortButton(sortDescriptionButton, Description, "Description");
        registerSortButton(sortPackageTypeButton, PackageType, "Package Type");
        registerSortButton(sortUnitButton, Unit, "Unit");
        registerSortButton(sortUnitsInAPackButton, UnitsInAPack, "Units In Pack");
        registerSortButton(sortPackageCostButton, PackageCost, "Package Cost");
        registerSortButton(sortAvailabilityButton, Availability, "Availability");
        registerSortButton(sortStockLimitButton, StockLimit, "Stock Limit");

        updateAllSortButtonTexts();
    }

    private void registerSortButton(Button button, TableColumn<Item, ?> column, String label) {
        sortButtonToColumn.put(button, column);
        sortButtonLabels.put(button, label);
        sortStates.put(button, SortState.INACTIVE);
    }

    @FXML
    private void handleSortButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        SortState currentState = sortStates.get(clickedButton);
        SortState nextState = nextState(currentState);

        for (Button button : sortStates.keySet()) {
            if (button != clickedButton) {
                sortStates.put(button, SortState.INACTIVE);
            }
        }

        sortStates.put(clickedButton, nextState);
        applySort(clickedButton, nextState);
        updateAllSortButtonTexts();
    }

    private SortState nextState(SortState currentState) {
        return switch (currentState) {
            case INACTIVE -> SortState.ASCENDING;
            case ASCENDING -> SortState.DESCENDING;
            case DESCENDING -> SortState.INACTIVE;
        };
    }

    private void applySort(Button button, SortState state) {
        TableColumn<Item, ?> column = sortButtonToColumn.get(button);

        if (column == null) {
            return;
        }

        column.setSortable(true);
        Catalogue.getSortOrder().clear();

        if (state == SortState.INACTIVE) {
            Catalogue.sort();
            return;
        }

        if (state == SortState.ASCENDING) {
            column.setSortType(TableColumn.SortType.ASCENDING);
        } else {
            column.setSortType(TableColumn.SortType.DESCENDING);
        }

        Catalogue.getSortOrder().add(column);
        Catalogue.sort();
    }

    private void updateAllSortButtonTexts() {
        for (Button button : sortStates.keySet()) {
            updateSortButtonText(button);
        }
    }

    private void updateSortButtonText(Button button) {
        String baseLabel = sortButtonLabels.get(button);
        SortState state = sortStates.get(button);

        String suffix = switch (state) {
            case INACTIVE -> "";
            case ASCENDING -> " ▲";
            case DESCENDING -> " ▼";
        };

        button.setText(baseLabel + suffix);
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    @FXML
    public void clearFilters(ActionEvent event) {
        searchField.clear();
        packageTypeFilter.setValue("All");
        unitFilter.setValue("All");
        maxPriceField.clear();
        inStockOnly.setSelected(false);
        lowStockOnly.setSelected(false);
        updateFilters();
    }

    @FXML
    public void addToCheckout(ActionEvent event) {
        Item selectedItem = Catalogue.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        int selectedQuantity = 1;

        try {
            String enteredText = quantity.getText();
            if (enteredText != null && !enteredText.isBlank()) {
                selectedQuantity = Integer.parseInt(enteredText.trim());
            }
        } catch (NumberFormatException e) {
            selectedQuantity = 1;
        }

        if (selectedQuantity <= 0) {
            selectedQuantity = 1;
        }

        checkout.put(selectedItem, selectedQuantity);
    }

    @FXML
    public void printCheckout(ActionEvent event) {
        for (Item item : checkout.keySet()) {
            System.out.println(item.getDescription() + " x " + checkout.get(item));
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
}