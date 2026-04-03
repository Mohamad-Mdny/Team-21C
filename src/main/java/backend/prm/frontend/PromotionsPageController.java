package backend.prm.frontend;

import backend.prm.controller.PromotionController;
import backend.prm.model.PromotionCampaign;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PromotionsPageController {

    @FXML
    private ListView<PromotionCampaign> campaignListView;

    @FXML
    private Label campaignCountLabel;

    private PromotionController promotionController;
    private final ObservableList<PromotionCampaign> allCampaigns = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML
    public void initialize() {
        if(promotionController == null) {
            setupController();
        }
        setupListView();
        loadActiveCampaigns();
    }

    public void setPromotionController(PromotionController promotionController) {
        this.promotionController = promotionController;
        loadActiveCampaigns();
    }

    private void setupController() {
        PromotionRepository repository = new PromotionRepository();
        PromotionService service = new PromotionService(repository);
        this.promotionController = new PromotionController(service);
    }

    private void setupListView() {
        campaignListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(PromotionCampaign campaign, boolean empty) {
                super.updateItem(campaign, empty);

                if (empty || campaign == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                VBox card = PromotionCardFactory.createCard(
                        campaign,
                        formatter,
                        () -> openCampaignDetails(campaign)
                );

                setText(null);
                setGraphic(card);
            }
        });
    }

    private void loadActiveCampaigns() {
        if (promotionController == null || campaignListView == null) {
            return;
        }
        List<PromotionCampaign> activeCampaigns =
                promotionController.getActiveCampaigns(LocalDateTime.now());

        allCampaigns.setAll(activeCampaigns);
        campaignListView.setItems(allCampaigns);
        updateCountLabel(allCampaigns.size());
    }

    @FXML
    private void handleRefresh() {
        loadActiveCampaigns();
    }

    private void updateCountLabel(int count) {
        if (count == 1) {
            campaignCountLabel.setText("1 active campaign");
        } else {
            campaignCountLabel.setText(count + " active campaigns");
        }
    }

    private void openCampaignDetails(PromotionCampaign campaign) {
        try {
            PromotionCampaign fullCampaign =
                    promotionController.viewCampaignAndRecordClick(campaign.getId(), LocalDateTime.now());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/PromotionDetailsPage.fxml"));
            Scene scene = new Scene(loader.load());

            PromotionDetailsPageController controller = loader.getController();
            controller.setPromotionController(promotionController);
            controller.setCampaign(fullCampaign);

            Stage stage = (Stage) campaignListView.getScene().getWindow();
            stage.setTitle("Promotion Details");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}