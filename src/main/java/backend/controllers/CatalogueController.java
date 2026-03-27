package backend.controllers;

import backend.DatabaseManager;
import backend.models.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


public class CatalogueController {
    @FXML
    TableView<Item> Catalogue;
    @FXML
    TableColumn ItemID;
    @FXML
    TableColumn Description;
    @FXML
    TableColumn PackageType;
    @FXML
    TableColumn Unit;
    @FXML
    TableColumn UnitsInAPack;
    @FXML
    TableColumn PackageCost;
    @FXML
    TableColumn Availability;
    @FXML
    TableColumn StockLimit;
    @FXML
    TextField quantity;
    HashMap<Item,Integer> checkout = new HashMap<>();

    @FXML
    void initialize(){
        //connects to database
        DatabaseManager databaseManager = new DatabaseManager();
        Connection connection = databaseManager.makeConnection();
        try{
            //sets up sql statement
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Catalogue");
            //Goes through the entire inventory
            while(resultSet.next()){
                //adds items to the table
                Catalogue.getItems().add(
                        new Item(resultSet.getInt("ItemID"),resultSet.getString("Description"),resultSet.getString("PackageType"),resultSet.getString("Unit"), resultSet.getInt("UnitsInAPack"),resultSet.getFloat("PackageCost"),resultSet.getInt("Availability"),resultSet.getInt("StockLimit")));
                System.out.println(resultSet.getFloat("PackageCost"));
            }
            //displays the table info within the cells accordingly
            ItemID.setCellValueFactory(new PropertyValueFactory<>("ItemID"));
            Description.setCellValueFactory(new PropertyValueFactory<>("Description"));
            PackageType.setCellValueFactory(new PropertyValueFactory<>("PackageType"));
            Unit.setCellValueFactory(new PropertyValueFactory<>("Unit"));
            UnitsInAPack.setCellValueFactory(new PropertyValueFactory<>("UnitsInAPack"));
            PackageCost.setCellValueFactory(new PropertyValueFactory<>("PackageCost"));
            Availability.setCellValueFactory(new PropertyValueFactory<>("Availability"));
            StockLimit.setCellValueFactory(new PropertyValueFactory<>("StockLimit"));

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    //adds selected item to the checkout hashmap
    public void addToCheckout(ActionEvent event){
        int quantity = Integer.parseInt(this.quantity.getText());
        if (quantity <=0){
            quantity = 1;
        }
        checkout.put(Catalogue.getSelectionModel().getSelectedItem(),quantity);
    }
    //prints hashmap to console
    public void printCheckout(ActionEvent event){
        for(Item item: checkout.keySet()){
            System.out.println(item.getDescription());
        };
    }

}
