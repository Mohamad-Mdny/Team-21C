package backend.controllers;

import backend.DatabaseManager;
import backend.Main;
import backend.models.Order;
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
    public TableView<Order> orderHistory;
    @FXML
    public TableColumn OrderID;
    @FXML
    public TableColumn description;
    @FXML
    public TableColumn orderEmailAddress;
    @FXML
    public TableView<Transaction> transactionHistory;
    @FXML
    public TableColumn transactionID;
    @FXML
    public TableColumn amount;
    @FXML
    public TableColumn transactionEmailAddress;


    public void initialize(){
        System.out.println("Loading Tables");
        loadOrderHistory();
        loadTransactionHistory();
    }

    void loadOrderHistory(){
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ipospu.order WHERE EmailAddress = ?");
            System.out.println(Main.member.getEmailAddress());
            statement.setString(1, Main.member.getEmailAddress());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                orderHistory.getItems().add(new Order(resultSet.getInt("OrderID"),resultSet.getString("Description"),resultSet.getString("EmailAddress")));
            }
            OrderID.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
            description.setCellValueFactory(new PropertyValueFactory<>("description"));
            orderEmailAddress.setCellValueFactory(new PropertyValueFactory<>("orderEmailAddress"));

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    void loadTransactionHistory(){
        DatabaseManager database = new DatabaseManager();
        Connection connection = database.makeConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ipospu.transaction where EmailAddress = ?");
            statement.setString(1, Main.member.getEmailAddress());
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
        switchPage(event, "CurrentPromotions.fxml");
    }
    @FXML public void goToBasket(ActionEvent event) {
        switchPage(event, "Basket.fxml");
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
