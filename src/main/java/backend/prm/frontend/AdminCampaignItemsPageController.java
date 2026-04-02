package backend.prm.frontend;

import backend.prm.controller.PromotionController;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
//import backend.prm.repository.ProductDAO;
import backend.prm.repository.PromotionRepository;
import backend.prm.service.PromotionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class AdminCampaignItemsPageController {

    @FXML
    private Label campaignTitleLabel;

    @FXML
    private ListView<PromotionItem> itemsListView;

    @FXML
    private TextField productIdField;

    @FXML
    private TextField discountField;

    @FXML
    private Label statusLabel;

    private final ObservableList<PromotionItem> items = FXCollections.observableArrayList();
    private final ProductDAO productDAO = new ProductDAO();

    private PromotionController promotionController;
    private PromotionCampaign campaign;

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
        campaignTitleLabel.setText("Campaign: " + campaign.getTitle() + " (ID: " + campaign.getId() + ")");
        loadItems();
    }

    private void setupListView() {
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

                String productInfo = "Unknown product";
                Optional<backend.prm.model.Product> productOpt = productDAO.findById(item.getProductId());
                if (productOpt.isPresent()) {
                    var product = productOpt.get();
                    productInfo = product.getDescription() + " | £" + product.getPackageCost();
                }

                setText(
                        "Item ID: " + item.getId() +
                                "\nProduct ID: " + item.getProductId() +
                                "\nProduct: " + productInfo +
                                "\nDiscount: " + item.getDiscountPercent() + "%"
                );
            }
        });

        itemsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                productIdField.setText(selected.getProductId());
                discountField.setText(String.valueOf(selected.getDiscountPercent()));
            }
        });
    }

    private void loadItems() {
        if (campaign == null) {
            return;
        }

        items.setAll(promotionController.getCampaignItems(campaign.getId()));
        statusLabel.setText("Loaded items: " + items.size());
    }

    @FXML
    private void handleAddItem() {
        if (campaign == null) {
            statusLabel.setText("Campaign is not set.");
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

            promotionController.addItemToCampaign(campaign.getId(), productId, discount);

            clearForm();
            loadItems();
            statusLabel.setText("Item added to database.");
        } catch (Exception e) {
            statusLabel.setText("Add failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteItem() {
        PromotionItem selected = itemsListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Select an item to delete.");
            return;
        }

        try {
            promotionController.deleteItem(selected.getId());
            clearForm();
            loadItems();
            statusLabel.setText("Item deleted from database.");
        } catch (Exception e) {
            statusLabel.setText("Delete failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadItems();
        statusLabel.setText("Items refreshed from database.");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) itemsListView.getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        productIdField.clear();
        discountField.clear();
        itemsListView.getSelectionModel().clearSelection();
    }
}