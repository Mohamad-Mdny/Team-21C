package backend.prm.frontend;

import backend.prm.frontend.PromotionProductView;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.IntConsumer;

public class PromotionProductCardFactory {

    public static VBox createCard(PromotionProductView item, IntConsumer onAddToOrder) {
        Label nameLabel = new Label(item.getProductName());
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #111827;");

        Label productIdLabel = new Label("Product ID: " + item.getProductId());
        productIdLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        Label originalPriceLabel = new Label(String.format("Original price: £%.2f", item.getOriginalPrice()));
        Label discountLabel = new Label(String.format("Discount: %.0f%%", item.getDiscountPercent()));
        Label discountedPriceLabel = new Label(String.format("Promo price: £%.2f", item.getDiscountedPrice()));
        Label statsLabel = new Label("Added: " + item.getAddedToOrderCount() + " | Purchased: " + item.getPurchasedCount());

        originalPriceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");
        discountLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #065f46; -fx-background-color: #d1fae5; -fx-background-radius: 999; -fx-padding: 6 10 6 10;");
        discountedPriceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #111827;");
        statsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        VBox priceBox = new VBox(10, originalPriceLabel, discountLabel, discountedPriceLabel, statsLabel);
        priceBox.setPadding(new Insets(14));
        priceBox.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 14;");

        Spinner<Integer> quantitySpinner = new Spinner<>();
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(90);

        Button addToOrderButton = new Button("Add to Order");
        addToOrderButton.setOnAction(event -> onAddToOrder.accept(quantitySpinner.getValue()));
        addToOrderButton.setStyle("-fx-background-color: #111827; -fx-text-fill: white; -fx-font-weight: 700; -fx-background-radius: 12; -fx-padding: 10 18 10 18;");

        HBox actionBox = new HBox(10, new Label("Qty"), quantitySpinner, addToOrderButton);
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-color: #e5e7eb; -fx-border-radius: 18;");
        card.getChildren().addAll(nameLabel, productIdLabel, priceBox, actionBox);
        return card;
    }
}
