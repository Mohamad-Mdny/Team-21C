package backend.prm.frontend;

import backend.Main;
import backend.models.ItemCell;
import backend.prm.controller.PromotionController;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
import backend.prm.repository.ProductDAO;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label statusLabel;
    @FXML private Label startLabel;
    @FXML private Label endLabel;
    @FXML private Label itemsCountLabel;
    @FXML private ListView<PromotionProductView> itemsListView;

    private final ProductDAO productDAO = new ProductDAO();
    private PromotionController promotionController;
    private PromotionCampaign campaign;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML
    public void initialize() {
        if (promotionController == null) {
            PromotionRepository repository = new PromotionRepository();
            PromotionService service = new PromotionService(repository);
            this.promotionController = new PromotionController(service);
        }
        setupListView();
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
                VBox card = PromotionProductCardFactory.createCard(item, quantity -> handleAddToOrder(item, quantity));
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
        descriptionLabel.setText(freshCampaign.getDescriptions() == null || freshCampaign.getDescriptions().isBlank()
                ? "No description available."
                : freshCampaign.getDescriptions());
        PromotionStatus status = freshCampaign.getStatus(LocalDateTime.now());
        statusLabel.setText("Status: " + status.name() + " | Clicks: " + freshCampaign.getClickCount());
        startLabel.setText("Start: " + formatDate(freshCampaign.getStartDateTime()));
        endLabel.setText("End: " + formatDate(freshCampaign.getEndDateTime()));

        List<PromotionProductView> productViews = promotionController.getCampaignItems(freshCampaign.getId())
                .stream()
                .map(this::mapToProductView)
                .collect(Collectors.toList());

        itemsListView.setItems(FXCollections.observableArrayList(productViews));
        updateItemsCount(productViews.size());
    }

    private PromotionProductView mapToProductView(PromotionItem item) {
        var productOpt = productDAO.findById(item.getItemId());
        String productName = productOpt.map(p -> p.getDescriptions()).orElse("Unknown product");
        float originalPrice = productOpt.map(p -> p.getPackageCost()).orElse(0.0f);

        double effectiveDiscount = item.getOverrideDiscountPercent() != null
                ? item.getOverrideDiscountPercent()
                : campaign.getDiscountPercent();

        return new PromotionProductView(
                item.getId(),
                item.getCampaignId(),
                item.getItemId(),
                productName,
                originalPrice,
                effectiveDiscount,
                item.getAddedToOrderCount(),
                item.getPurchasedCount()
        );
    }

    private void handleAddToOrder(PromotionProductView item, int quantity) {
        try {
            var productOpt = productDAO.findById(item.getProductId());

            if (productOpt.isEmpty()) {
                showError("Product not found.");
                return;
            }

            ItemCell baseItem = productOpt.get();

            ItemCell promoItem = new ItemCell(
                    Integer.parseInt(baseItem.getItemID()),
                    baseItem.getDescriptions(),
                    baseItem.getPackageType(),
                    baseItem.getUnit(),
                    baseItem.getUnitsInAPack(),
                    (float) item.getDiscountedPrice(),
                    baseItem.getAvailability(),
                    baseItem.getStockLimit()
            );

            for (int i = 0; i < quantity; i++) {
                Main.m.getBasket().add(promoItem);
            }

            promotionController.addItemToOrder(item.getCampaignId(), item.getItemId(), quantity);
            PromotionBasketTracker.add(item.getCampaignId(), item.getItemId(), quantity);

            showInfo("Success", quantity + " x " + item.getProductName() + " added to order.");
            loadCampaignDetails();

        } catch (Exception e) {
            showError("Cannot add item to order: " + e.getMessage());
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
            showError("Unable to open promotions page: " + e.getMessage());
        }
    }

    private void updateItemsCount(int count) {
        itemsCountLabel.setText(count == 1 ? "1 product in this campaign" : count + " products in this campaign");
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(formatter);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
