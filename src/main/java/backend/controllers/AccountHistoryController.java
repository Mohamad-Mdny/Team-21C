package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import backend.models.OrderCell;
import backend.models.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class AccountHistoryController {

    @FXML
    private TextField searchField;

    @FXML
    public TableView<OrderCell> orderHistory;
    @FXML
    public TableColumn OrderID;
    @FXML
    public TableColumn Descriptions;
    @FXML
    public TableColumn EmailAddress;
    @FXML
    public TableColumn DeliveryAddress;

    @FXML
    public TableView<Transaction> transactionHistory;
    @FXML
    public TableColumn transactionID;
    @FXML
    public TableColumn amount;
    @FXML
    public TableColumn transactionEmailAddress;


    public void initialize(){
        loadOrderHistory();
        loadTransactionHistory();
    }

    void loadOrderHistory(){
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalogue.orders WHERE EmailAddress = ?");
            statement.setString(1, Main.member.getUserName());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                orderHistory.getItems().add(new OrderCell(resultSet.getString("OrderID") ,resultSet.getString(2),resultSet.getString("EmailAddress"), resultSet.getString("Address")));
            }// Dont replace 2 with descirptions, it doesnt work, dont touch this i hate this
            OrderID.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
            Descriptions.setCellValueFactory(new PropertyValueFactory<>("Descriptions"));
            EmailAddress.setCellValueFactory(new PropertyValueFactory<>("OrderEmailAddress"));
            DeliveryAddress.setCellValueFactory(new PropertyValueFactory<>("DeliveryAddress"));

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    void loadTransactionHistory(){
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM catalogue.transaction where EmailAddress = ?");
            statement.setString(1, Main.member.getUserName());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                transactionHistory.getItems().add(new Transaction(resultSet.getInt("TransactionID"),resultSet.getInt("Amount"),resultSet.getString("EmailAddress")));
            }
            transactionID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
            amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            transactionEmailAddress.setCellValueFactory(new PropertyValueFactory<>("transactionEmailAddress"));


        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSearchEnter(ActionEvent event) {
        String text = searchField.getText();

        if (text != null && !text.isBlank()) {
            CatalogueController.pendingSearchText = text.trim();
            switchPage(event, "Catalogue.fxml");
        }
    }

    @FXML public void goToCatalogue(ActionEvent event) {
        switchPage(event, "Catalogue.fxml");
    }
    @FXML public void goToCurrentPromotions(ActionEvent event) {
        switchPage(event, "PromotionsPage.fxml");
    }
    @FXML public void goToBasket(ActionEvent event) {
        switchPage(event, "Basket.fxml");
    }
    @FXML public void goToAccountSettings(ActionEvent event){
        switchPage(event, "AccountSettings.fxml");
    }

    private void switchPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("/frontend/" + fxmlFile))
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

