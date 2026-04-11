package backend.prm.frontend;

import backend.prm.model.PromotionCampaign;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PromotionCardFactory {

    public static VBox createCard(PromotionCampaign campaign,
                                  DateTimeFormatter formatter,
                                  Runnable onViewDetails) {

        Label statusLabel = new Label(campaign.getStatus(LocalDateTime.now()).name());
        statusLabel.setStyle(
                "-fx-background-color: #e5e7eb;" +
                        "-fx-text-fill: #374151;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-padding: 6 12 6 12;" +
                        "-fx-background-radius: 999;"
        );

        Label titleLabel = new Label(campaign.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-text-fill: #111827;"
        );

        Label descriptionLabel = new Label(
                campaign.getDescriptions() == null ? "No description available." : campaign.getDescriptions()
        );
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #4b5563;" +
                        "-fx-line-spacing: 3;"
        );

        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(14));
        infoBox.setStyle(
                "-fx-background-color: #f9fafb;" +
                        "-fx-background-radius: 14;"
        );

        Label startLabel = new Label("Start: " + formatDate(campaign.getStartDateTime(), formatter));
        Label endLabel = new Label("End: " + formatDate(campaign.getEndDateTime(), formatter));
        Label clicksLabel = new Label("Campaign clicks: " + campaign.getClickCount());

        startLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");
        endLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");
        clicksLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

        infoBox.getChildren().addAll(startLabel, endLabel, clicksLabel);

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setOnAction(event -> onViewDetails.run());
        viewDetailsButton.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: 700;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 18 10 18;"
        );

        Button saveButton = new Button("Save");
        saveButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #374151;" +
                        "-fx-font-weight: 700;" +
                        "-fx-border-color: #d1d5db;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 18 10 18;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonRow = new HBox(10, viewDetailsButton);

        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 18;"
        );

        card.getChildren().addAll(
                statusLabel,
                titleLabel,
                descriptionLabel,
                infoBox,
                buttonRow
        );

        return card;
    }

    private static String formatDate(java.time.LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(formatter);
    }
}