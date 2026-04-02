package backend.prm.frontend;

import backend.prm.controller.PromotionController;
import backend.prm.model.Product;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
//import backend.prm.repository.ProductDAO;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class AdminDashboardController {

    @FXML
    private ListView<PromotionCampaign> campaignListView;

    @FXML
    private ListView<PromotionItem> itemsListView;

    @FXML
    private TabPane tabPane;

    @FXML
    private Label selectedCampaignLabel;

    @FXML
    private Label itemsCampaignLabel;

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
    private HBox campaignMainActionsBox;

    @FXML
    private HBox campaignEditActionsBox;

    @FXML
    private Button createCampaignButton;

    @FXML
    private Button editCampaignButton;

    @FXML
    private Button deleteCampaignButton;

    @FXML
    private Button cancelCampaignButton;

    @FXML
    private Button saveCampaignChangesButton;

    @FXML
    private Button discardCampaignChangesButton;

    @FXML
    private TextField productIdField;

    @FXML
    private TextField discountField;
    @FXML
    private HBox itemEditActionsBox;

    @FXML
    private Button addItemButton;

    @FXML
    private Button editItemButton;

    @FXML
    private Button deleteItemButton;

    @FXML
    private Button saveItemChangesButton;

    @FXML
    private Button cancelItemChangesButton;

    private final ObservableList<PromotionCampaign> campaigns = FXCollections.observableArrayList();
    private final ObservableList<PromotionItem> items = FXCollections.observableArrayList();

    //private final ProductDAO productDAO = new ProductDAO();

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
        loadCampaigns();
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

                setText(
                        campaign.getTitle() +
                                "\nStatus: " + status +
                                "\nStart: " + campaign.getStartDateTime() +
                                "\nEnd: " + campaign.getEndDateTime()
                );
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

                String productText = "Unknown product";
//                Optional<Product> productOpt = productDAO.findById(item.getProductId());
//                if (productOpt.isPresent()) {
//                    Product product = productOpt.get();
//                    productText = product.getDescription() + " | £" + product.getPackageCost();
//                }

                setText(
                        "Item ID: " + item.getId() +
                                "\nProduct ID: " + item.getProductId() +
                                "\nProduct: " + productText +
                                "\nDiscount: " + item.getDiscountPercent() + "%"
                );
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

    private void setItemEditMode(boolean enabled) {
        itemEditMode = enabled;

        itemEditActionsBox.setVisible(enabled);
        itemEditActionsBox.setManaged(enabled);

        addItemButton.setDisable(enabled);
        editItemButton.setDisable(enabled);
        deleteItemButton.setDisable(enabled);
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

        if (campaign.getStartDateTime() != null) {
            startDatePicker.setValue(campaign.getStartDateTime().toLocalDate());
        } else {
            startDatePicker.setValue(null);
        }

        if (campaign.getEndDateTime() != null) {
            endDatePicker.setValue(campaign.getEndDateTime().toLocalDate());
        } else {
            endDatePicker.setValue(null);
        }
    }

    private void loadItemsForSelectedCampaign() {
        if (selectedCampaign == null) {
            items.clear();
            return;
        }

        items.setAll(promotionController.getCampaignItems(selectedCampaign.getId()));
    }

    private void clearCampaignForm() {
        titleField.clear();
        descriptionArea.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        selectedCampaignLabel.setText("Creating new campaign");
    }

    private void clearItemForm() {
        productIdField.clear();
        discountField.clear();
        itemsListView.getSelectionModel().clearSelection();
        selectedItem = null;
    }

    private void setFormDisabled(boolean disabled) {
        titleField.setDisable(disabled);
        descriptionArea.setDisable(disabled);
        startDatePicker.setDisable(disabled);
        endDatePicker.setDisable(disabled);
    }

    @FXML
    private void handleRefreshCampaigns() {
        loadCampaigns();
        statusLabel.setText("Campaign list refreshed.");
    }

    @FXML
    private void handleCreateNew() {
        campaignListView.getSelectionModel().clearSelection();
        selectedCampaign = null;
        createMode = true;
        editMode = false;
        clearCampaignForm();
        setCampaignEditMode(true);
        tabPane.getSelectionModel().select(0);
        statusLabel.setText("Create mode enabled.");
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
        statusLabel.setText("Edit mode enabled.");
    }

    @FXML
    private void handleSaveChanges() {
        try {
            String title = titleField.getText();
            String description = descriptionArea.getText();

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate == null || endDate == null) {
                statusLabel.setText("Choose start and end dates.");
                return;
            }

            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(0, 0));
            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.of(23, 59, 59));

            if (createMode) {
                promotionController.createCampaign(title, description, startDateTime, endDateTime);
                statusLabel.setText("New campaign saved to database.");
            } else if (editMode && selectedCampaign != null) {
                promotionController.updateCampaign(
                        selectedCampaign.getId(),
                        title,
                        description,
                        startDateTime,
                        endDateTime
                );
                statusLabel.setText("Campaign changes saved to database.");
            } else {
                statusLabel.setText("Nothing to save.");
                return;
            }

            createMode = false;
            editMode = false;
            setCampaignEditMode(false);
            loadCampaigns();
            clearCampaignForm();
        } catch (Exception e) {
            statusLabel.setText("Save failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelChanges() {
        createMode = false;
        editMode = false;
        setCampaignEditMode(false);

        if (selectedCampaign != null) {
            populateCampaignDetails(selectedCampaign);
            statusLabel.setText("Changes discarded.");
        } else {
            clearCampaignForm();
            selectedCampaignLabel.setText("No campaign selected");
            statusLabel.setText("Create cancelled.");
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

            selectedCampaign = promotionController.getCampaignById(selectedCampaign.getId());
            populateCampaignDetails(selectedCampaign);
            loadCampaigns();

            statusLabel.setText("Campaign status changed to CANCELLED.");
        } catch (Exception e) {
            statusLabel.setText("Cancel campaign failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCampaign() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Campaign");
        alert.setContentText("Are you sure you want to delete this campaign and all its items?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == yesButton) {
            try {
                promotionController.deleteCampaign(selectedCampaign.getId());

                selectedCampaign = null;
                campaigns.clear();
                items.clear();
                clearCampaignForm();
                clearItemForm();
                selectedCampaignLabel.setText("No campaign selected");
                itemsCampaignLabel.setText("No campaign selected");
                loadCampaigns();
                statusLabel.setText("Campaign deleted from database.");
            } catch (Exception e) {
                statusLabel.setText("Delete failed: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Delete cancelled.");
        }
    }

    @FXML
    private void handleAddItem() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign first.");
            return;
        }

        try {
            String productId = productIdField.getText();
            String discountText = discountField.getText();

            if (productId == null || productId.isBlank()) {
                statusLabel.setText("Enter product ID.");
                return;
            }

            double discount = Double.parseDouble(discountText);

            promotionController.addItemToCampaign(selectedCampaign.getId(), productId, discount);

            clearItemForm();
            loadItemsForSelectedCampaign();
            setItemEditMode(false);
            statusLabel.setText("Item added to database.");
        } catch (Exception e) {
            statusLabel.setText("Add item failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteItem() {
        PromotionItem selectedItem = itemsListView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            statusLabel.setText("Select an item to delete.");
            return;
        }

        try {
            promotionController.deleteItem(selectedItem.getId());
            clearItemForm();
            loadItemsForSelectedCampaign();
            setItemEditMode(false);
            statusLabel.setText("Item deleted from database.");
        } catch (Exception e) {
            statusLabel.setText("Delete item failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditItemMode() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign first.");
            return;
        }

        if (selectedItem == null) {
            statusLabel.setText("Select an item first.");
            return;
        }

        setItemEditMode(true);
        statusLabel.setText("Item edit mode enabled.");
    }

    @FXML
    private void handleSaveItemChanges() {
        if (selectedCampaign == null) {
            statusLabel.setText("Select a campaign first.");
            return;
        }

        if (!itemEditMode || selectedItem == null) {
            statusLabel.setText("No item is being edited.");
            return;
        }

        try {
            String productId = productIdField.getText();
            String discountText = discountField.getText();

            if (productId == null || productId.isBlank()) {
                statusLabel.setText("Enter product ID.");
                return;
            }

            double discount = Double.parseDouble(discountText);

            promotionController.updateItem(
                    selectedCampaign.getId(),
                    selectedItem.getId(),
                    productId,
                    discount
            );

            loadItemsForSelectedCampaign();
            clearItemForm();
            setItemEditMode(false);
            statusLabel.setText("Item changes saved to database.");
        } catch (Exception e) {
            statusLabel.setText("Save item failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelItemChanges() {
        if (selectedItem != null) {
            productIdField.setText(selectedItem.getProductId());
            discountField.setText(String.valueOf(selectedItem.getDiscountPercent()));
            statusLabel.setText("Item edit cancelled.");
        } else {
            clearItemForm();
            statusLabel.setText("Nothing to cancel.");
        }

        setItemEditMode(false);
    }

    @FXML
    private void handleRefreshItems() {
        loadItemsForSelectedCampaign();
        statusLabel.setText("Items refreshed.");
    }
}