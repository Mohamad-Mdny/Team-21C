package backend.prm.frontend;

import backend.prm.controller.PromotionController;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
//import backend.prm.repository.ProductDAO;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PromotionDetailsPageController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label startLabel;

    @FXML
    private Label endLabel;

    @FXML
    private Label itemsCountLabel;

    @FXML
    private ListView<PromotionProductView> itemsListView;

   // private final ProductDAO productDAO = new ProductDAO();
    private PromotionController promotionController;
    private PromotionCampaign campaign;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML
    public void initialize() {
        setupController();
        setupListView();
    }

    private void setupController() {
        PromotionRepository repository = new PromotionRepository();
        PromotionService service = new PromotionService(repository);
        this.promotionController = new PromotionController(service);
    }

    public void setPromotionController(PromotionController promotionController) {
        this.promotionController = promotionController;
    }

    public void setCampaign(PromotionCampaign campaign) {
        this.campaign = campaign;
        loadCampaignDetails();
    }

    private void setupListView() {
        itemsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(PromotionProductView item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                VBox card = PromotionProductCardFactory.createCard(
                        item,
                        () -> handleAddToOrder(item)
                );

                setText(null);
                setGraphic(card);
            }
        });
    }

    private void loadCampaignDetails() {
        if (campaign == null) {
            return;
        }

        PromotionCampaign freshCampaign = promotionController.getCampaignById(campaign.getId());

        titleLabel.setText(freshCampaign.getTitle());
        descriptionLabel.setText(
                freshCampaign.getDescription() == null || freshCampaign.getDescription().isBlank()
                        ? "No description available."
                        : freshCampaign.getDescription()
        );

        PromotionStatus status = freshCampaign.getStatus(LocalDateTime.now());
        statusLabel.setText("Status: " + status.name());
        startLabel.setText("Start: " + formatDate(freshCampaign.getStartDateTime()));
        endLabel.setText("End: " + formatDate(freshCampaign.getEndDateTime()));

        List<PromotionItem> items = promotionController.getCampaignItems(freshCampaign.getId());

//        List<PromotionProductView> productViews = items.stream()
//                .map(this::mapToProductView)
//                .collect(Collectors.toList());

//        itemsListView.setItems(FXCollections.observableArrayList(productViews));
//        updateItemsCount(productViews.size());
    }

//    private PromotionProductView mapToProductView(PromotionItem item) {
//        var productOpt = productDAO.findById(item.getProductId());
//
//        if (productOpt.isEmpty()) {
//            return new PromotionProductView(
//                    item.getId(),
//                    item.getCampaignId(),
//                    item.getProductId(),
//                    "Unknown product",
//                    0.0,
//                    item.getDiscountPercent()
//            );
//        }
//
//        var product = productOpt.get();
//
//        return new PromotionProductView(
//                item.getId(),
//                item.getCampaignId(),
//                item.getProductId(),
//                product.getDescription(),
//                product.getPackageCost(),
//                item.getDiscountPercent()
//        );
//    }

    private void handleAddToOrder(PromotionProductView item) {
        try {
            promotionController.addItemToOrder(item.getCampaignId(), item.getItemId(), 1);
            System.out.println("Added to order: product " + item.getProductName());
        } catch (Exception e) {
            System.out.println("Cannot add item to order: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadCampaignDetails();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontend/PromotionsPage.fxml"));
            Scene scene = new Scene(loader.load());

            PromotionsPageController controller = loader.getController();
            controller.setPromotionController(promotionController);

            Stage stage = (Stage) itemsListView.getScene().getWindow();
            stage.setTitle("Active Promotions");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateItemsCount(int count) {
        if (count == 1) {
            itemsCountLabel.setText("1 product in this campaign");
        } else {
            itemsCountLabel.setText(count + " products in this campaign");
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(formatter);
    }
}
