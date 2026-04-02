package backend.prm.frontend;

import backend.prm.controller.PromotionController;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionStatus;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AdminPromotionsPageController {

    @FXML
    private ListView<PromotionCampaign> campaignListView;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label statusLabel;

    private PromotionController promotionController;
    private final ObservableList<PromotionCampaign> campaigns = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupController();
        setupListView();
        loadAllCampaigns();
    }

    private void setupController() {
        PromotionRepository repository = new PromotionRepository();
        PromotionService service = new PromotionService(repository);
        this.promotionController = new PromotionController(service);
    }

    private void setupListView() {
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
                        "ID: " + campaign.getId() +
                                " | " + campaign.getTitle() +
                                "\nStatus: " + status +
                                "\nStart: " + campaign.getStartDateTime() +
                                "\nEnd: " + campaign.getEndDateTime()
                );
            }
        });

        campaignListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                fillForm(selected);
            }
        });
    }

    private void fillForm(PromotionCampaign campaign) {
        titleField.setText(campaign.getTitle());
        descriptionArea.setText(campaign.getDescription());

        if (campaign.getStartDateTime() != null) {
            startDatePicker.setValue(campaign.getStartDateTime().toLocalDate());
        }

        if (campaign.getEndDateTime() != null) {
            endDatePicker.setValue(campaign.getEndDateTime().toLocalDate());
        }
    }

    private void loadAllCampaigns() {
        campaigns.setAll(promotionController.getAllCampaigns());
        statusLabel.setText("Loaded campaigns: " + campaigns.size());
    }

    @FXML
    private void handleCreateCampaign() {
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

            promotionController.createCampaign(title, description, startDateTime, endDateTime);

            clearForm();
            loadAllCampaigns();
            statusLabel.setText("Campaign created and saved to database.");
        } catch (Exception e) {
            statusLabel.setText("Create failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCampaign() {
        PromotionCampaign selected = campaignListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Select a campaign to delete.");
            return;
        }

        try {
            promotionController.deleteCampaign(selected.getId());
            loadAllCampaigns();
            clearForm();
            statusLabel.setText("Campaign deleted from database.");
        } catch (Exception e) {
            statusLabel.setText("Delete failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateCampaign() {
        PromotionCampaign selected = campaignListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Select a campaign to update.");
            return;
        }

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

            promotionController.updateCampaign(
                    selected.getId(),
                    title,
                    description,
                    startDateTime,
                    endDateTime
            );

            loadAllCampaigns();
            statusLabel.setText("Campaign updated in database.");
        } catch (Exception e) {
            statusLabel.setText("Update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageItems() {
        PromotionCampaign selected = campaignListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Select a campaign first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/AdminCampaignItemsPage.fxml"));
            Scene scene = new Scene(loader.load());

            AdminCampaignItemsPageController controller = loader.getController();
            controller.setPromotionController(promotionController);
            controller.setCampaign(selected);

            Stage stage = new Stage();
            stage.setTitle("Manage Campaign Items");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Failed to open items page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadAllCampaigns();
        statusLabel.setText("List refreshed from database.");
    }

    private void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        campaignListView.getSelectionModel().clearSelection();
    }
}