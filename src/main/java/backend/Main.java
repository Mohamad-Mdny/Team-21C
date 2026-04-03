package backend;

import backend.Reports.ProductStats;
import backend.Reports.SalesReport;
import backend.models.Member;
import backend.models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;


public class Main extends Application {
    public static User m = new User();
    public static Member member;

    @Override

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/frontend/Catalogue.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Stuff");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //Report creation
        List<ProductStats> stats = List.of(new ProductStats("123456","Widget A", 42, 23.45), new ProductStats("123454654","Widget B", 17, 34.34), new ProductStats("129989","Widget C", 89, 67.45));
        SalesReport.generateReport(stats, "sales_report.pdf", "01/01/2026", "31/03/2026");
        //dont remove
        launch(args);

    }


}
