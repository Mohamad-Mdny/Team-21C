package backend.prm.frontend;

import backend.prm.controller.PromotionController;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
import backend.prm.report.CampaignHitReportRow;
import backend.prm.report.CampaignReportRow;
import backend.prm.report.SalesReportRow;
import backend.prm.repository.ProductDAO;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController {

    @FXML
    private VBox rootPane;

    @FXML
    private ListView<PromotionCampaign> campaignListView;
    @FXML
    private Label selectedCampaignLabel;
    @FXML
    private Label statusLabel;

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField campaignDiscountField;

    @FXML
    private Button createCampaignButton;
    @FXML
    private Button editCampaignButton;
    @FXML
    private Button saveCampaignButton;
    @FXML
    private Button cancelCampaignButton;
    @FXML
    private Button deleteCampaignButton;
    @FXML
    private Button reactivateCampaignButton;
    @FXML
    private Button stopCampaignButton;

    @FXML
    private ListView<PromotionItem> itemsListView;
    @FXML
    private Label itemsCampaignLabel;

    @FXML
    private VBox itemDetailsBox;
    @FXML
    private Label selectedItemIdLabel;
    @FXML
    private Label selectedItemProductIdLabel;
    @FXML
    private Label selectedItemPriceLabel;
    @FXML
    private Label selectedItemStatsLabel;
    @FXML
    private Button deleteItemButton;
    @FXML
    private TextField editItemProductIdField;
    @FXML
    private Button saveItemButton;
    @FXML
    private Button cancelItemEditButton;

    @FXML
    private Label toastLabel;

    @FXML
    private TextField productSearchField;
    @FXML
    private TableView<ProductDAO.ProductSummary> productsTable;
    @FXML
    private TableColumn<ProductDAO.ProductSummary, String> productIdColumn;
    @FXML
    private TableColumn<ProductDAO.ProductSummary, String> productDescriptionColumn;
    @FXML
    private TableColumn<ProductDAO.ProductSummary, Double> productPriceColumn;
    @FXML
    private Button addSelectedProductButton;

    @FXML
    private DatePicker reportStartDatePicker;
    @FXML
    private DatePicker reportEndDatePicker;

    @FXML
    private Label reportTitleLabel;
    @FXML
    private Label reportAddressLabel;
    @FXML
    private VBox reportMetaBox;
    @FXML
    private TextArea reportBodyArea;
    @FXML
    private Label reportGeneratedLabel;
    @FXML
    private Label reportGeneratedByLabel;

    private static final String COMPANY_ADDRESS =
            "Cosymed Ltd.\n" +
                    "27 Sainsbury Close,\n" +
                    "3, High Level Drive,\n" +
                    "Sydenham,\n" +
                    "SE26 3ET\n" +
                    "Phone: 0208 778 0124\n" +
                    "Fax: 0208 778 0125";

    private final ObservableList<PromotionCampaign> campaigns = FXCollections.observableArrayList();
    private final ObservableList<PromotionItem> items = FXCollections.observableArrayList();
    private final ObservableList<ProductDAO.ProductSummary> products = FXCollections.observableArrayList();

    private final ProductDAO productDAO = new ProductDAO();
    private final DateTimeFormatter reportDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter reportDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private PromotionController promotionController;
    private PromotionCampaign selectedCampaign;
    private PromotionItem selectedItem;

    private boolean createMode = false;
    private boolean editMode = false;

    private PauseTransition toastTimer;

    @FXML
    public void initialize() {
        setupController();
        setupCampaignList();
        setupItemsList();
        setupProductsTable();
        setupDefaults();

        loadCampaigns();
        loadProducts();

        clearItemSelection();
        setCampaignFormDisabled(true);
        refreshUiState();
    }

    private void setupController() {
        PromotionRepository repository = new PromotionRepository();
        PromotionService service = new PromotionService(repository);
        this.promotionController = new PromotionController(service);
    }

    private void setupDefaults() {
        reportStartDatePicker.setValue(LocalDate.now().minusDays(30));
        reportEndDatePicker.setValue(LocalDate.now());

        itemDetailsBox.setVisible(false);
        itemDetailsBox.setManaged(false);

        if (toastLabel != null) {
            toastLabel.setVisible(false);
            toastLabel.setManaged(false);
        }

        if (reportAddressLabel != null) {
            reportAddressLabel.setText(COMPANY_ADDRESS);
        }
        if (reportBodyArea != null) {
            reportBodyArea.setEditable(false);
            reportBodyArea.setWrapText(false);
        }
        renderEmptyReportState();
    }

    private void setupCampaignList() {
        campaignListView.setItems(campaigns);

        campaignListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(PromotionCampaign campaign, boolean empty) {
                super.updateItem(campaign, empty);
                if (empty || campaign == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                PromotionStatus status = campaign.getStatus(LocalDateTime.now());
                setText(
                        campaign.getCampaignCode() + " | " + campaign.getTitle()
                                + "\nStatus: " + status
                                + " | Discount: " + campaign.getDiscountPercent() + "%"
                                + "\nStart: " + campaign.getStartDateTime()
                                + "\nEnd: " + campaign.getEndDateTime()
                );
            }
        });

        campaignListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedCampaign = newVal;

            if (newVal == null) {
                createMode = false;
                editMode = false;
                clearCampaignForm();
                items.clear();
                itemsCampaignLabel.setText("No campaign selected");
                selectedCampaignLabel.setText("No campaign selected");
                setCampaignFormDisabled(true);
                clearItemSelection();
                refreshUiState();
                return;
            }

            createMode = false;
            editMode = false;
            populateCampaignDetails(newVal);
            loadItemsForSelectedCampaign();
            setCampaignFormDisabled(true);
            clearItemSelection();
            refreshUiState();
        });
    }

    private void setupItemsList() {
        itemsListView.setItems(items);

        itemsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(PromotionItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                String discountInfo = String.format("%.2f", selectedCampaign.getDiscountPercent());

                setText(
                        "Item ID: " + item.getId()
                                + "\nProduct ID: " + item.getItemId()
                                + "\nCampaign discount: " + discountInfo + "%"
                                + "\nPromo price: £" + String.format("%.2f", item.getPromotionalPrice())
                                + "\nAdded: " + item.getAddedToOrderCount()
                                + " | Purchased: " + item.getPurchasedCount()
                );
            }
        });

        itemsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedItem = newVal;
            if (newVal == null) {
                clearItemSelection();
            } else {
                showItemDetails(newVal);
            }
            refreshUiState();
        });
    }

    private void setupProductsTable() {
        productIdColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getProductId()));
        productDescriptionColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getDescription()));
        productPriceColumn.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getPackageCost()));

        productsTable.setItems(products);

        productSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                loadProducts();
            } else {
                products.setAll(productDAO.searchActiveProducts(newVal));
            }
        });
    }

    private void loadCampaigns() {
        campaigns.setAll(promotionController.getAllCampaigns());
        statusLabel.setText("Loaded campaigns: " + campaigns.size());
    }

    private void loadProducts() {
        products.setAll(productDAO.findAllActiveProducts());
    }

    private void populateCampaignDetails(PromotionCampaign campaign) {
        selectedCampaignLabel.setText("Selected campaign: " + campaign.getTitle() + " (ID: " + campaign.getId() + ")");
        itemsCampaignLabel.setText("Items in: " + campaign.getTitle());

        titleField.setText(campaign.getTitle());
        descriptionArea.setText(campaign.getDescription());
        campaignDiscountField.setText(String.valueOf(campaign.getDiscountPercent()));

        startDatePicker.setValue(campaign.getStartDateTime() == null ? null : campaign.getStartDateTime().toLocalDate());
        endDatePicker.setValue(campaign.getEndDateTime() == null ? null : campaign.getEndDateTime().toLocalDate());
    }

    private void loadItemsForSelectedCampaign() {
        if (selectedCampaign == null) {
            items.clear();
            return;
        }
        items.setAll(promotionController.getCampaignItems(selectedCampaign.getId()));
    }

    @FXML
    private void handleRefreshCampaigns() {
        Long selectedCampaignId = selectedCampaign == null ? null : selectedCampaign.getId();

        loadCampaigns();

        if (selectedCampaignId != null) {
            selectCampaignById(selectedCampaignId);
        }

        refreshUiState();
        showToast("Campaign list refreshed.", true);
    }

    @FXML
    private void handleCreateNewCampaign() {
        selectedCampaign = null;
        campaignListView.getSelectionModel().clearSelection();

        createMode = true;
        editMode = false;

        clearCampaignForm();
        items.clear();
        clearItemSelection();

        selectedCampaignLabel.setText("Creating new campaign");
        itemsCampaignLabel.setText("No campaign selected");

        setCampaignFormDisabled(false);
        statusLabel.setText("Create mode enabled.");
        refreshUiState();
    }

    @FXML
    private void handleEditCampaign() {
        if (selectedCampaign == null) {
            showToast("Select a campaign first.", false);
            return;
        }

        createMode = false;
        editMode = true;
        setCampaignFormDisabled(false);
        statusLabel.setText("Edit mode enabled.");
        refreshUiState();
    }

    @FXML
    private void handleSaveCampaign() {
        try {
            String title = titleField.getText() == null ? "" : titleField.getText().trim();
            if (title.isBlank()) {
                throw new IllegalArgumentException("Campaign title is required.");
            }

            LocalDateTime startDateTime = startOfDay(startDatePicker, "start");
            LocalDateTime endDateTime = endOfDay(endDatePicker, "end");

            if (startDateTime.isAfter(endDateTime)) {
                throw new IllegalArgumentException("End date must be after start date.");
            }

            double discount = parseDiscountOrZero();

            if (createMode) {
                PromotionCampaign created = promotionController.createCampaign(
                        title,
                        descriptionArea.getText(),
                        startDateTime,
                        endDateTime,
                        discount
                );

                createMode = false;
                editMode = false;
                setCampaignFormDisabled(true);

                loadCampaigns();
                selectCampaignById(created.getId());
                refreshUiState();

                statusLabel.setText("Campaign created.");
                showToast("Campaign created successfully.", true);
                return;
            }

            if (editMode && selectedCampaign != null) {
                long campaignId = selectedCampaign.getId();

                promotionController.updateCampaign(
                        campaignId,
                        title,
                        descriptionArea.getText(),
                        startDateTime,
                        endDateTime,
                        discount
                );

                createMode = false;
                editMode = false;
                setCampaignFormDisabled(true);

                loadCampaigns();
                selectCampaignById(campaignId);
                loadItemsForSelectedCampaign();
                refreshUiState();

                statusLabel.setText("Campaign updated.");
                showToast("Campaign updated successfully.", true);
            }
        } catch (Exception e) {
            statusLabel.setText("Save failed (1): " + e.getMessage());
            showToast("Save failed (2) : " + e.getMessage(), false);
            throw e;
        }
    }

    @FXML
    private void handleCancelCampaignEdit() {
        createMode = false;
        editMode = false;
        setCampaignFormDisabled(true);

        if (selectedCampaign != null) {
            populateCampaignDetails(selectedCampaign);
        } else {
            clearCampaignForm();
            selectedCampaignLabel.setText("No campaign selected");
        }

        refreshUiState();
        statusLabel.setText("Edit cancelled.");
        showToast("Edit cancelled.", true);
    }

    @FXML
    private void handleDeleteCampaign() {
        if (selectedCampaign == null) {
            showToast("Select a campaign to delete.", false);
            return;
        }

        if (!confirm("Delete campaign", "Delete selected campaign and all its items?")) {
            return;
        }

        try {
            promotionController.deleteCampaign(selectedCampaign.getId());

            selectedCampaign = null;
            createMode = false;
            editMode = false;

            items.clear();
            clearCampaignForm();
            clearItemSelection();
            setCampaignFormDisabled(true);

            loadCampaigns();
            refreshUiState();

            statusLabel.setText("Campaign deleted.");
            showToast("Campaign deleted successfully.", true);
        } catch (Exception e) {
            statusLabel.setText("Delete failed: " + e.getMessage());
            showToast("Delete failed: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleStopCampaign() {
        if (selectedCampaign == null) {
            showToast("Select a campaign first.", false);
            return;
        }

        try {
            long campaignId = selectedCampaign.getId();

            promotionController.cancelCampaign(campaignId);

            loadCampaigns();
            selectCampaignById(campaignId);
            loadItemsForSelectedCampaign();
            refreshUiState();

            statusLabel.setText("Campaign cancelled.");
            showToast("Campaign cancelled successfully.", true);
        } catch (Exception e) {
            statusLabel.setText("Cancel failed: " + e.getMessage());
            showToast("Cancel failed: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleReactivateCampaign() {
        if (selectedCampaign == null) {
            showToast("Select a campaign first.", false);
            return;
        }

        try {
            long campaignId = selectedCampaign.getId();

            promotionController.reactivateCampaign(campaignId);

            loadCampaigns();
            selectCampaignById(campaignId);
            loadItemsForSelectedCampaign();
            refreshUiState();

            statusLabel.setText("Campaign reactivated.");
            showToast("Campaign reactivated successfully.", true);
        } catch (Exception e) {
            statusLabel.setText("Reactivate failed: " + e.getMessage());
            showToast("Reactivate failed: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleAddSelectedProductToCampaign() {
        if (selectedCampaign == null) {
            showToast("Select a campaign first.", false);
            return;
        }

        ProductDAO.ProductSummary product = productsTable.getSelectionModel().getSelectedItem();
        if (product == null) {
            showToast("Select a product from the list.", false);
            return;
        }

        try {
            promotionController.addItemToCampaign(selectedCampaign.getId(), product.getProductId());
            loadItemsForSelectedCampaign();
            refreshUiState();

            statusLabel.setText("Product added to campaign: " + product.getProductId());
            showToast("Product added to campaign.", true);
        } catch (Exception e) {
            statusLabel.setText("Add item failed: " + e.getMessage());
            showToast("Add item failed: " + e.getMessage(), false);
            throw e;
        }
    }

    @FXML
    private void handleDeleteSelectedItem() {
        if (selectedItem == null) {
            showToast("Select an item to delete.", false);
            return;
        }

        if (!confirm("Delete item", "Delete selected campaign item?")) {
            return;
        }

        try {
            promotionController.deleteItem(selectedItem.getId());
            loadItemsForSelectedCampaign();
            closeItemDetails();
            refreshUiState();

            statusLabel.setText("Item deleted.");
            showToast("Item deleted successfully.", true);
        } catch (Exception e) {
            statusLabel.setText("Delete item failed: " + e.getMessage());
            showToast("Delete item failed: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleSaveItemChanges() {
        if (selectedCampaign == null) {
            showToast("Select a campaign first.", false);
            return;
        }

        if (selectedItem == null) {
            showToast("Select an item first.", false);
            return;
        }

        try {
            String newProductId = editItemProductIdField.getText();

            if (newProductId == null || newProductId.isBlank()) {
                showToast("Enter product ID.", false);
                return;
            }

            long itemId = selectedItem.getId();

            promotionController.updateItem(
                    selectedCampaign.getId(),
                    itemId,
                    newProductId
            );

            loadItemsForSelectedCampaign();

            PromotionItem refreshedItem = findItemById(itemId);
            if (refreshedItem != null) {
                selectedItem = refreshedItem;
                itemsListView.getSelectionModel().select(refreshedItem);
                showItemDetails(refreshedItem);
            } else {
                closeItemDetails();
            }

            refreshUiState();
            statusLabel.setText("Item updated.");
            showToast("Item updated successfully.", true);
        } catch (Exception e) {
            statusLabel.setText("Item update failed: " + e.getMessage());
            showToast("Item update failed: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleCancelItemEdit() {
        closeItemDetails();
        statusLabel.setText("Item details closed.");
        showToast("Item details closed.", true);
    }

    @FXML
    private void handleRootClick(MouseEvent event) {
        Node target = (Node) event.getTarget();

        if (isChildOf(target, itemsListView) || isChildOf(target, itemDetailsBox)) {
            return;
        }

        closeItemDetails();
    }

    private void showItemDetails(PromotionItem item) {
        selectedItemIdLabel.setText(String.valueOf(item.getId()));
        selectedItemProductIdLabel.setText(item.getItemId());
        selectedItemPriceLabel.setText("£" + String.format("%.2f", item.getPromotionalPrice()));
        selectedItemStatsLabel.setText(
                "Added: " + item.getAddedToOrderCount() + " | Purchased: " + item.getPurchasedCount()
        );

        if (editItemProductIdField != null) {
            editItemProductIdField.setText(item.getItemId());
        }

        itemDetailsBox.setVisible(true);
        itemDetailsBox.setManaged(true);
    }

    private void clearItemSelection() {
        closeItemDetails();
    }

    private void closeItemDetails() {
        selectedItem = null;
        itemsListView.getSelectionModel().clearSelection();

        selectedItemIdLabel.setText("-");
        selectedItemProductIdLabel.setText("-");
        selectedItemPriceLabel.setText("-");
        selectedItemStatsLabel.setText("-");

        if (editItemProductIdField != null) {
            editItemProductIdField.clear();
        }

        itemDetailsBox.setVisible(false);
        itemDetailsBox.setManaged(false);
    }

    private boolean isChildOf(Node node, Node parent) {
        Node current = node;
        while (current != null) {
            if (current == parent) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private void setCampaignFormDisabled(boolean disabled) {
        titleField.setDisable(disabled);
        descriptionArea.setDisable(disabled);
        startDatePicker.setDisable(disabled);
        endDatePicker.setDisable(disabled);
        campaignDiscountField.setDisable(disabled);
    }

    private void refreshUiState() {
        updateActionButtons();
        updateCampaignButtonsVisibility();
    }

    private void updateActionButtons() {
        boolean hasCampaign = selectedCampaign != null;
        boolean hasItem = selectedItem != null;

        editCampaignButton.setDisable(!hasCampaign);
        deleteCampaignButton.setDisable(!hasCampaign);
        stopCampaignButton.setDisable(!hasCampaign);
        reactivateCampaignButton.setDisable(!hasCampaign);
        addSelectedProductButton.setDisable(!hasCampaign);

        deleteItemButton.setDisable(!hasItem);

        if (saveItemButton != null) {
            saveItemButton.setDisable(!hasItem);
        }

        if (cancelItemEditButton != null) {
            cancelItemEditButton.setDisable(!hasItem);
        }
    }

    private void updateCampaignButtonsVisibility() {
        boolean hasCampaign = selectedCampaign != null;
        boolean editing = createMode || editMode;

        editCampaignButton.setVisible(!editing && hasCampaign);
        editCampaignButton.setManaged(!editing && hasCampaign);

        deleteCampaignButton.setVisible(!editing && hasCampaign);
        deleteCampaignButton.setManaged(!editing && hasCampaign);

        saveCampaignButton.setVisible(editing);
        saveCampaignButton.setManaged(editing);

        cancelCampaignButton.setVisible(editing);
        cancelCampaignButton.setManaged(editing);

        if (!editing || !hasCampaign) {
            stopCampaignButton.setVisible(false);
            stopCampaignButton.setManaged(false);
            reactivateCampaignButton.setVisible(false);
            reactivateCampaignButton.setManaged(false);
            return;
        }

        PromotionStatus status = selectedCampaign.getStatus(LocalDateTime.now());

        boolean canStop = status == PromotionStatus.ACTIVE || status == PromotionStatus.SCHEDULED;
        boolean canReactivate = status == PromotionStatus.CANCELLED;

        stopCampaignButton.setVisible(canStop);
        stopCampaignButton.setManaged(canStop);

        reactivateCampaignButton.setVisible(canReactivate);
        reactivateCampaignButton.setManaged(canReactivate);
    }

    private void showToast(String message, boolean success) {
        if (toastLabel == null) {
            return;
        }

        if (toastTimer != null) {
            toastTimer.stop();
        }

        toastLabel.setText(message);
        toastLabel.setVisible(true);
        toastLabel.setManaged(true);

        if (success) {
            toastLabel.setStyle(
                    "-fx-padding: 8 12 8 12;" +
                            "-fx-background-radius: 8;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: #dcfce7;" +
                            "-fx-text-fill: #166534;" +
                            "-fx-border-color: #86efac;" +
                            "-fx-border-radius: 8;"
            );
        } else {
            toastLabel.setStyle(
                    "-fx-padding: 8 12 8 12;" +
                            "-fx-background-radius: 8;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-color: #fee2e2;" +
                            "-fx-text-fill: #991b1b;" +
                            "-fx-border-color: #fca5a5;" +
                            "-fx-border-radius: 8;"
            );
        }

        toastTimer = new PauseTransition(Duration.seconds(2.5));
        toastTimer.setOnFinished(e -> {
            toastLabel.setVisible(false);
            toastLabel.setManaged(false);
        });
        toastTimer.play();
    }

    private LocalDateTime startOfDay(DatePicker picker, String label) {
        if (picker.getValue() == null) {
            throw new IllegalArgumentException("Choose " + label + " date.");
        }
        return picker.getValue().atStartOfDay();
    }

    private LocalDateTime endOfDay(DatePicker picker, String label) {
        if (picker.getValue() == null) {
            throw new IllegalArgumentException("Choose " + label + " date.");
        }
        return picker.getValue().atTime(23, 59, 59);
    }

    @FXML
    private void handleGenerateSalesReport() {
        try {
            LocalDateTime from = startOfDay(reportStartDatePicker, "report start");
            LocalDateTime to = endOfDay(reportEndDatePicker, "report end");
            List<SalesReportRow> rows = promotionController.getSalesReport(from, to);

            updateReportPreview(
                    "IPOS-PU Sales Report",
                    List.of(
                            "Start Period: " + from.format(reportDateFormatter),
                            "End Period: " + to.format(reportDateFormatter)
                    ),
                    buildSalesReportBody(rows)
            );

            showToast("Sales report generated.", true);
        } catch (Exception e) {
            e.printStackTrace();
            renderReportError("Sales report failed:\n" + e.getMessage());
            showToast("Sales report failed.", false);
        }
    }

    @FXML
    private void handleGenerateCampaignReport() {
        try {
            LocalDateTime from = startOfDay(reportStartDatePicker, "report start");
            LocalDateTime to = endOfDay(reportEndDatePicker, "report end");
            List<CampaignReportRow> rows = promotionController.getCampaignReport(from, to);

            updateReportPreview(
                    "IPOS-PU Advert Campaigns Report",
                    List.of(
                            "Start Period: " + from.format(reportDateFormatter),
                            "End Period: " + to.format(reportDateFormatter),
                            "Active campaigns: " + rows.size()
                    ),
                    buildCampaignReportBody(rows)
            );

            showToast("Campaign report generated.", true);
        } catch (Exception e) {
            e.printStackTrace();
            renderReportError("Campaign report failed:\n" + e.getMessage());
            showToast("Campaign report failed.", false);
        }
    }

    @FXML
    private void handleGenerateHitsReport() {
        try {
            LocalDateTime from = startOfDay(reportStartDatePicker, "report start");
            LocalDateTime to = endOfDay(reportEndDatePicker, "report end");
            List<CampaignHitReportRow> rows = promotionController.getCampaignHitReport(from, to);

            String campaignCode = rows.isEmpty() ? "-" : safe(rows.get(0).getCampaignCode());
            String campaignTitle = rows.isEmpty() ? "-" : safe(rows.get(0).getCampaignTitle());

            updateReportPreview(
                    "Customer Campaign Engagement Report",
                    List.of(
                            "Campaign ID: " + campaignCode,
                            "Campaign Description: " + campaignTitle,
                            "Start Period: " + from.format(reportDateFormatter),
                            "End Period: " + to.format(reportDateFormatter)
                    ),
                    buildHitsReportBody(rows)
            );

            showToast("Hits report generated.", true);
        } catch (Exception e) {
            e.printStackTrace();
            renderReportError("Hits report failed:\n" + e.getMessage());
            showToast("Hits report failed.", false);
        }
    }

    private void renderEmptyReportState() {
        if (reportTitleLabel != null) {
            reportTitleLabel.setText("Report preview");
        }
        if (reportMetaBox != null) {
            reportMetaBox.getChildren().clear();
            Label hint = new Label("Choose a period and click one of the report buttons.");
            hint.setWrapText(true);
            hint.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
            reportMetaBox.getChildren().add(hint);
        }
        if (reportBodyArea != null) {
            reportBodyArea.setText("");
        }
        if (reportGeneratedLabel != null) {
            reportGeneratedLabel.setText("");
        }
        if (reportGeneratedByLabel != null) {
            reportGeneratedByLabel.setText("");
        }
    }

    private void renderReportError(String message) {
        if (reportTitleLabel != null) {
            reportTitleLabel.setText("Report preview");
        }
        if (reportMetaBox != null) {
            reportMetaBox.getChildren().clear();
        }
        if (reportBodyArea != null) {
            reportBodyArea.setText(message);
        }
        if (reportGeneratedLabel != null) {
            reportGeneratedLabel.setText("");
        }
        if (reportGeneratedByLabel != null) {
            reportGeneratedByLabel.setText("");
        }
    }

    private void updateReportPreview(String title, List<String> metaLines, String body) {
        reportTitleLabel.setText(title);
        reportMetaBox.getChildren().clear();

        for (String line : metaLines) {
            Label label = new Label(line);
            label.setWrapText(true);
            label.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827;");
            reportMetaBox.getChildren().add(label);
        }

        reportBodyArea.setText(body);
        reportGeneratedLabel.setText("Generated: " + LocalDate.now().format(reportDateFormatter));
        reportGeneratedByLabel.setText("Generated by IPOS-PU Operations");
    }

    private String buildSalesReportBody(List<SalesReportRow> rows) {
        if (rows.isEmpty()) {
            return "No sales found for this period.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-24s %12s %14s %12s%n",
                "Item ID", "Description", "Sold, packs", "Unit price, £", "Total, £"));
        sb.append("-".repeat(78)).append(System.lineSeparator());

        int totalQuantity = 0;
        double totalRevenue = 0.0;

        for (SalesReportRow row : rows) {
            totalQuantity += row.getQuantitySold();
            totalRevenue += row.getTotalPrice();

            sb.append(String.format("%-12s %-24s %12d %14.2f %12.2f%n",
                    safe(row.getProductId()),
                    truncate(row.getProductDescription(), 24),
                    row.getQuantitySold(),
                    row.getUnitPrice(),
                    row.getTotalPrice()));
        }

        sb.append("-".repeat(78)).append(System.lineSeparator());
        sb.append(String.format("%-37s %12d %14s %12.2f",
                "Total online sales for period",
                totalQuantity,
                "",
                totalRevenue));

        return sb.toString();
    }

    private String buildCampaignReportBody(List<CampaignReportRow> rows) {
        if (rows.isEmpty()) {
            return "No campaign data found for this period.";
        }

        StringBuilder sb = new StringBuilder();

        for (CampaignReportRow row : rows) {
            sb.append(String.format("%-18s %s%n", "Campaign ID", safe(row.getCampaignCode())));
            sb.append(String.format("%-18s %s%n", "Title", safe(row.getCampaignTitle())));
            sb.append(String.format("%-18s %s%n", "Start Date/Time", formatDateTime(row.getStartDateTime())));
            sb.append(String.format("%-18s %s%n", "End Date/Time", formatDateTime(row.getEndDateTime())));
            sb.append(String.format("%-18s %s%n", "Status", safe(row.getStatus())));
            sb.append(String.format("%-18s %d%n", "Campaign hits", row.getClickCount()));
            sb.append(String.format("%-18s %d%n", "Added to order", row.getTotalAdded()));
            sb.append(String.format("%-18s %d%n", "Purchased", row.getTotalPurchased()));

            double conversion = row.getClickCount() == 0
                    ? 0.0
                    : (row.getTotalPurchased() * 100.0) / row.getClickCount();
            sb.append(String.format("%-18s %.2f%%%n", "Conversion", conversion));
            sb.append(System.lineSeparator());
            sb.append("-".repeat(72)).append(System.lineSeparator()).append(System.lineSeparator());
        }

        return sb.toString().trim();
    }

    private String buildHitsReportBody(List<CampaignHitReportRow> rows) {
        if (rows.isEmpty()) {
            return "No engagement data found for this period.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-24s %12s %12s %16s%n",
                "Counter ID", "Counter description", "Hits count", "Purchases", "Conversion Rate"));
        sb.append("-".repeat(84)).append(System.lineSeparator());

        for (CampaignHitReportRow row : rows) {
            sb.append(String.format("%-12s %-24s %12d %12d %15.2f%%%n",
                    safe(row.getProductId()),
                    truncate(row.getProductDescription() + " hits", 24),
                    row.getCampaignClicks(),
                    row.getPurchased(),
                    row.getConversionRate()));
        }

        return sb.toString();
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(reportDateTimeFormatter);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void selectCampaignById(long campaignId) {
        campaignListView.getSelectionModel().clearSelection();

        for (PromotionCampaign campaign : campaigns) {
            if (campaign.getId() == campaignId) {
                campaignListView.getSelectionModel().select(campaign);
                selectedCampaign = campaign;
                populateCampaignDetails(campaign);
                return;
            }
        }

        selectedCampaign = null;
    }

    private PromotionItem findItemById(long itemId) {
        for (PromotionItem item : items) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }

    private double parseDiscount() {
        try {
            double value = Double.parseDouble(campaignDiscountField.getText());
            if (value < 0 || value > 100) {
                throw new IllegalArgumentException("Discount must be between 0 and 100.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid discount percent.");
        }
    }

    private boolean confirm(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void clearCampaignForm() {
        titleField.clear();
        descriptionArea.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        campaignDiscountField.clear();
        selectedCampaignLabel.setText("No campaign selected");
    }

    private double parseDiscountOrZero() {
        String text = campaignDiscountField.getText();
        if (text == null || text.isBlank()) {
            return 0.0;
        }
        try {
            double value = Double.parseDouble(text.trim());
            if (value < 0 || value > 100) {
                statusLabel.setText("Discount must be between 0 and 100.");
                showToast("Discount must be between 0 and 100.", false);
            }
            return value;
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid Discount Percentage " + e.getMessage());
            showToast("Invalid Discount Percentage" + e.getMessage(), false);
            return 0;
        }
    }
}
