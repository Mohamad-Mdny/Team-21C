package backend.prm.frontend;

import backend.prm.controller.PromotionController;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
import backend.prm.report.CampaignHitReportRow;
import backend.prm.report.CampaignReportRow;
import backend.prm.report.SalesReportRow;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController {

    @FXML private ListView<PromotionCampaign> campaignListView;
    @FXML private ListView<PromotionItem> itemsListView;
    @FXML private TabPane tabPane;
    @FXML private Label selectedCampaignLabel;
    @FXML private Label itemsCampaignLabel;
    @FXML private Label statusLabel;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private HBox campaignMainActionsBox;
    @FXML private HBox campaignEditActionsBox;
    @FXML private Button reactivateCampaignButton;;
    @FXML private Button editCampaignButton;
    @FXML private Button deleteCampaignButton;
    @FXML private Button cancelCampaignButton;
    @FXML private Button saveCampaignChangesButton;
    @FXML private Button discardCampaignChangesButton;
    @FXML private TextField productIdField;
    @FXML private TextField discountField;
    @FXML private HBox itemEditActionsBox;
    @FXML private Button addItemButton;
    @FXML private Button editItemButton;
    @FXML private Button deleteItemButton;
    @FXML private Button saveItemChangesButton;
    @FXML private Button cancelItemChangesButton;

    @FXML private DatePicker reportStartDatePicker;
    @FXML private DatePicker reportEndDatePicker;
    @FXML private TextField reportStartTimeField;
    @FXML private TextField reportEndTimeField;
    @FXML private TextArea reportOutputArea;

    private final ObservableList<PromotionCampaign> campaigns = FXCollections.observableArrayList();
    private final ObservableList<PromotionItem> items = FXCollections.observableArrayList();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private PromotionController promotionController;
    private PromotionCampaign selectedCampaign;
    private PromotionItem selectedItem;
    private boolean itemEditMode = false;
    private boolean createMode = false;
    private boolean editMode = false;

    @FXML
    public void initialize() {
        setupController();
        setupCampaignList();
        setupItemsList();
        setCampaignEditMode(false);
        setItemEditMode(false);
        startTimeField.setText("00:00");
        endTimeField.setText("23:59");
        reportStartTimeField.setText("00:00");
        reportEndTimeField.setText("23:59");
        reportStartDatePicker.setValue(LocalDate.now().minusDays(30));
        reportEndDatePicker.setValue(LocalDate.now());
        loadCampaigns();
        updateCampaignActionButtons();
    }

    private void setupController() {
        PromotionRepository repository = new PromotionRepository();
        PromotionService service = new PromotionService(repository);
        this.promotionController = new PromotionController(service);
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
                setText(campaign.getCampaignCode() + " | " + campaign.getTitle()
                        + "\nStatus: " + status
                        + " | Clicks: " + campaign.getClickCount()
                        + "\nStart: " + campaign.getStartDateTime()
                        + "\nEnd: " + campaign.getEndDateTime());
            }
        });
        campaignListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                selectedCampaign = newValue;
                populateCampaignDetails(newValue);
                loadItemsForSelectedCampaign();
                createMode = false;
                editMode = false;
                setCampaignEditMode(false);
                updateCampaignActionButtons();
            }
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
                setText("Item ID: " + item.getId()
                        + "\nProduct ID: " + item.getProductId()
                        + "\nDiscount: " + item.getDiscountPercent() + "%"
                        + "\nPromo price: £" + String.format("%.2f", item.getPromotionalPrice())
                        + "\nAdded: " + item.getAddedToOrderCount() + " | Purchased: " + item.getPurchasedCount());
            }
        });
        itemsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selectedItem = newValue;
            if (newValue != null) {
                productIdField.setText(newValue.getProductId());
                discountField.setText(String.valueOf(newValue.getDiscountPercent()));
            }
        });
    }

    private void setCampaignEditMode(boolean enabled) {
        campaignEditActionsBox.setVisible(enabled);
        campaignEditActionsBox.setManaged(enabled);
        campaignMainActionsBox.setVisible(!enabled);
        campaignMainActionsBox.setManaged(!enabled);
        setFormDisabled(!enabled);
    }

    private void setFormDisabled(boolean disabled) {
        titleField.setDisable(disabled);
        descriptionArea.setDisable(disabled);
        startDatePicker.setDisable(disabled);
        endDatePicker.setDisable(disabled);
        startTimeField.setDisable(disabled);
        endTimeField.setDisable(disabled);
    }

    private void setItemEditMode(boolean enabled) {
        itemEditMode = enabled;
        itemEditActionsBox.setVisible(enabled);
        itemEditActionsBox.setManaged(enabled);
        addItemButton.setDisable(enabled);
        editItemButton.setDisable(enabled);
        deleteItemButton.setDisable(enabled);
        productIdField.setDisable(!enabled && selectedCampaign != null);
        discountField.setDisable(!enabled && selectedCampaign != null);
    }

    private void loadCampaigns() {
        campaigns.setAll(promotionController.getAllCampaigns());
        statusLabel.setText("Loaded campaigns: " + campaigns.size());
    }

    private void populateCampaignDetails(PromotionCampaign campaign) {
        selectedCampaignLabel.setText("Selected campaign: " + campaign.getTitle() + " (ID: " + campaign.getId() + ")");
        itemsCampaignLabel.setText("Items for: " + campaign.getTitle());
        titleField.setText(campaign.getTitle());
        descriptionArea.setText(campaign.getDescription());
        startDatePicker.setValue(campaign.getStartDateTime() == null ? null : campaign.getStartDateTime().toLocalDate());
        endDatePicker.setValue(campaign.getEndDateTime() == null ? null : campaign.getEndDateTime().toLocalDate());
        startTimeField.setText(campaign.getStartDateTime() == null ? "00:00" : campaign.getStartDateTime().toLocalTime().format(timeFormatter));
        endTimeField.setText(campaign.getEndDateTime() == null ? "23:59" : campaign.getEndDateTime().toLocalTime().format(timeFormatter));
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
        loadCampaigns();
        if (selectedCampaign != null) {
            selectedCampaign = promotionController.getCampaignById(selectedCampaign.getId());
            populateCampaignDetails(selectedCampaign);
            loadItemsForSelectedCampaign();
        }
    }

    @FXML
    private void handleCreateNew() {
        clearCampaignForm();
        selectedCampaign = null;
        createMode = true;
        editMode = false;
        selectedCampaignLabel.setText("Creating new campaign");
        setCampaignEditMode(true);
        updateCampaignActionButtons();
    }

    @FXML
    private void handleEditMode() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign first.");
            return;
        }
        createMode = false;
        editMode = true;
        setCampaignEditMode(true);
        updateCampaignActionButtons();
    }

    @FXML
    private void handleSaveChanges() {
        try {
            LocalDateTime startDateTime = parseDateTime(startDatePicker.getValue(), startTimeField.getText(), "start");
            LocalDateTime endDateTime = parseDateTime(endDatePicker.getValue(), endTimeField.getText(), "end");
            if (createMode) {
                PromotionCampaign created = promotionController.createCampaign(
                        titleField.getText(),
                        descriptionArea.getText(),
                        startDateTime,
                        endDateTime
                );
                statusLabel.setText("Campaign created: " + created.getCampaignCode());
                loadCampaigns();
                selectedCampaign = created;
                campaignListView.getSelectionModel().select(created);
                populateCampaignDetails(created);
            } else if (editMode && selectedCampaign != null) {
                promotionController.updateCampaign(selectedCampaign.getId(), titleField.getText(), descriptionArea.getText(), startDateTime, endDateTime);
                statusLabel.setText("Campaign updated.");
            }
            loadCampaigns();
            setCampaignEditMode(false);
            createMode = false;
            editMode = false;
        } catch (Exception e) {
            statusLabel.setText("Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelChanges() {
        createMode = false;
        editMode = false;
        setCampaignEditMode(false);
        if (selectedCampaign != null) {
            populateCampaignDetails(selectedCampaign);
        } else {
            clearCampaignForm();
        }
    }

    @FXML
    private void handleDeleteCampaign() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign to delete.");
            return;
        }
        if (!confirm("Delete campaign", "Delete selected campaign and all its items?")) {
            return;
        }
        try {
            promotionController.deleteCampaign(selectedCampaign.getId());
            selectedCampaign = null;
            items.clear();
            clearCampaignForm();
            loadCampaigns();
            statusLabel.setText("Campaign deleted.");
        } catch (Exception e) {
            statusLabel.setText("Delete failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelCampaign() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign first.");
            return;
        }
        try {
            promotionController.cancelCampaign(selectedCampaign.getId());
            loadCampaigns();
            selectedCampaign = promotionController.getCampaignById(selectedCampaign.getId());
            populateCampaignDetails(selectedCampaign);
            statusLabel.setText("Campaign cancelled.");
        } catch (Exception e) {
            statusLabel.setText("Cancel failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddItem() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign first.");
            return;
        }
        try {
            promotionController.addItemToCampaign(selectedCampaign.getId(), productIdField.getText(), Double.parseDouble(discountField.getText()));
            loadItemsForSelectedCampaign();
            clearItemForm();
            statusLabel.setText("Item added to campaign.");
        } catch (Exception e) {
            statusLabel.setText("Add item failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditItemMode() {
        if (selectedItem == null) {
            statusLabel.setText("Select an item first.");
            return;
        }
        setItemEditMode(true);
    }

    @FXML
    private void handleSaveItemChanges() {
        if (selectedCampaign == null || selectedItem == null) {
            statusLabel.setText("Select a campaign item first.");
            return;
        }
        try {
            promotionController.updateItem(selectedCampaign.getId(), selectedItem.getId(), productIdField.getText(), Double.parseDouble(discountField.getText()));
            loadItemsForSelectedCampaign();
            setItemEditMode(false);
            statusLabel.setText("Item updated.");
        } catch (Exception e) {
            statusLabel.setText("Item update failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelItemChanges() {
        setItemEditMode(false);
        if (selectedItem != null) {
            productIdField.setText(selectedItem.getProductId());
            discountField.setText(String.valueOf(selectedItem.getDiscountPercent()));
        }
    }
    private void updateCampaignActionButtons() {
        if (selectedCampaign == null) {
            cancelCampaignButton.setVisible(false);
            cancelCampaignButton.setManaged(false);
            reactivateCampaignButton.setVisible(false);
            reactivateCampaignButton.setManaged(false);
            return;
        }

        PromotionStatus status = selectedCampaign.getStatus(LocalDateTime.now());

        boolean canCancel = status == PromotionStatus.ACTIVE || status == PromotionStatus.SCHEDULED;
        boolean canReactivate = status == PromotionStatus.CANCELLED;

        cancelCampaignButton.setVisible(canCancel);
        cancelCampaignButton.setManaged(canCancel);

        reactivateCampaignButton.setVisible(canReactivate);
        reactivateCampaignButton.setManaged(canReactivate);
    }

    @FXML
    private void handleReactivateCampaign() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign first.");
            return;
        }

        try {
            promotionController.reactivateCampaign(selectedCampaign.getId());
            loadCampaigns();
            selectedCampaign = promotionController.getCampaignById(selectedCampaign.getId());
            populateCampaignDetails(selectedCampaign);
            updateCampaignActionButtons();
            statusLabel.setText("Campaign reactivated.");
        } catch (Exception e) {
            statusLabel.setText("Reactivate failed: " + e.getMessage());
        }
    }
    @FXML
    private void handleDeleteItem() {
        if (selectedItem == null) {
            statusLabel.setText("Select an item to delete.");
            return;
        }
        if (!confirm("Delete item", "Delete selected campaign item?")) {
            return;
        }
        try {
            promotionController.deleteItem(selectedItem.getId());
            loadItemsForSelectedCampaign();
            clearItemForm();
            statusLabel.setText("Item deleted.");
        } catch (Exception e) {
            statusLabel.setText("Delete item failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshItems() {
        loadItemsForSelectedCampaign();
    }

    @FXML
    private void handleGenerateSalesReport() {
        try {
            LocalDateTime from = parseDateTime(reportStartDatePicker.getValue(), reportStartTimeField.getText(), "report start");
            LocalDateTime to = parseDateTime(reportEndDatePicker.getValue(), reportEndTimeField.getText(), "report end");
            List<SalesReportRow> rows = promotionController.getSalesReport(from, to);
            StringBuilder sb = new StringBuilder();
            sb.append("SALES REPORT\nPeriod: ").append(from).append(" to ").append(to).append("\n\n");
            sb.append(String.format("%-12s %-30s %-10s %-12s %-12s%n", "Product ID", "Description", "Qty Sold", "Unit Price", "Total"));
            sb.append("-".repeat(84)).append("\n");
            for (SalesReportRow row : rows) {
                sb.append(String.format("%-12s %-30s %-10d %-12.2f %-12.2f%n",
                        row.getProductId(), trim(row.getProductDescription(), 30), row.getQuantitySold(), row.getUnitPrice(), row.getTotalPrice()));
            }
            reportOutputArea.setText(sb.toString());
        } catch (Exception e) {
            reportOutputArea.setText("Sales report failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateCampaignReport() {
        try {
            LocalDateTime from = parseDateTime(reportStartDatePicker.getValue(), reportStartTimeField.getText(), "report start");
            LocalDateTime to = parseDateTime(reportEndDatePicker.getValue(), reportEndTimeField.getText(), "report end");
            List<CampaignReportRow> rows = promotionController.getCampaignReport(from, to);
            StringBuilder sb = new StringBuilder();
            sb.append("ADVERTISING CAMPAIGNS REPORT\nPeriod: ").append(from).append(" to ").append(to).append("\n\n");
            for (CampaignReportRow row : rows) {
                sb.append(row.getCampaignCode()).append(" | ").append(row.getCampaignTitle()).append("\n")
                        .append("Start: ").append(row.getStartDateTime()).append(" | End: ").append(row.getEndDateTime()).append("\n")
                        .append("Status: ").append(row.getStatus())
                        .append(" | Clicks: ").append(row.getClickCount())
                        .append(" | Added: ").append(row.getTotalAdded())
                        .append(" | Purchased: ").append(row.getTotalPurchased())
                        .append("\n\n");
            }
            reportOutputArea.setText(sb.toString());
        } catch (Exception e) {
            reportOutputArea.setText("Campaign report failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateHitsReport() {
        try {
            LocalDateTime from = parseDateTime(reportStartDatePicker.getValue(), reportStartTimeField.getText(), "report start");
            LocalDateTime to = parseDateTime(reportEndDatePicker.getValue(), reportEndTimeField.getText(), "report end");
            List<CampaignHitReportRow> rows = promotionController.getCampaignHitReport(from, to);
            StringBuilder sb = new StringBuilder();
            sb.append("CAMPAIGN HITS / CONVERSION REPORT\nPeriod: ").append(from).append(" to ").append(to).append("\n\n");
            sb.append(String.format("%-12s %-18s %-12s %-24s %-8s %-8s %-10s %-12s%n",
                    "Code", "Campaign", "Product ID", "Description", "Clicks", "Added", "Purchased", "Conversion"));
            sb.append("-".repeat(120)).append("\n");
            for (CampaignHitReportRow row : rows) {
                sb.append(String.format("%-12s %-18s %-12s %-24s %-8d %-8d %-10d %-12.2f%n",
                        row.getCampaignCode(), trim(row.getCampaignTitle(), 18), row.getProductId(), trim(row.getProductDescription(), 24),
                        row.getCampaignClicks(), row.getAddedToOrder(), row.getPurchased(), row.getConversionRate()));
            }
            reportOutputArea.setText(sb.toString());
        } catch (Exception e) {
            reportOutputArea.setText("Hits report failed: " + e.getMessage());
        }
    }

    private LocalDateTime parseDateTime(LocalDate date, String timeText, String label) {
        if (date == null) {
            throw new IllegalArgumentException("Choose " + label + " date.");
        }
        try {
            LocalTime time = LocalTime.parse(timeText == null || timeText.isBlank() ? "00:00" : timeText, timeFormatter);
            return LocalDateTime.of(date, time);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid " + label + " time. Use HH:mm.");
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
        startTimeField.setText("00:00");
        endTimeField.setText("23:59");
    }

    private void clearItemForm() {
        productIdField.clear();
        discountField.clear();
        selectedItem = null;
    }

    private String trim(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }
}
