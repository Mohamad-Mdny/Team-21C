package backend;

import backend.APIs.*;
import backend.Reports.*;
import backend.models.Admin;
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
    public static Admin admin;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/frontend/Catalogue.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("IPOS-PU");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)  {

        //SalesReport creation
//        List<ProductStats> stats = List.of(new ProductStats("123456","Widget A", 42, 23.45), new ProductStats("123454654","Widget B", 17, 34.34), new ProductStats("129989","Widget C", 89, 67.45));
//        SalesReport.generateReport(stats, "sales_report.pdf", "01/01/2026", "31/03/2026");


        //Campagin report Creation
//            List<CampaignItem> Camp1items = List.of(
//                    new CampaignItem("1", "Aspirin", "5%", 20, 9.50),
//                    new CampaignItem("2", "Chemical1", "5%", 200, 133.23),
//                    new CampaignItem("3", "chemical2", "5%", 20, 12.30)
//            );

//            List<CampaignItem> Camp2items = List.of(
//                    new CampaignItem("1", "Aspirin", "7%", 20, 9.50),
//                    new CampaignItem("2", "Chemical1", "7%", 200, 133.23),
//                    new CampaignItem("3", "chemical2", "7%", 20, 12.30),
//                    new CampaignItem("4", "chemical3", "7%", 400, 15.30)
//            );
//
//            List<CampaignStats> campaigns = List.of(
//                    new CampaignStats("Campaign 1", "01/03/2025", "31/03/2025", 3, "Fixed, 5%", Camp1items),
//                    new CampaignStats("Campaign 2", "01/04/2025", "15/05/2025", 4, "Fixed, 7%", Camp2items)
//            );
//
//            CampaignReport.generateReport(campaigns, "campaign_report.pdf", "01/03/2025", "31/05/2025");



//        Engagement report Creation
//        List<EngagementStats> engagement = List.of(
//                new EngagementStats("01", "Campaign", 3000, 0),
//                new EngagementStats("02", "Aspirin", 2000, 50),
//                new EngagementStats("03", "Analgin", 1000, 10),
//                new EngagementStats("04", "Iodine tincture", 200, 20)
//        );
//
//        EngagementReport.generateReport(engagement, "Campaign 1", "March Advertising Campaign, 3 items, Fixed Discount of 5%", "01/03/2025", "31/03/2025", "engagement_reportMain.pdf");
        try {
            new backend.APIs.EmailAPI(8085).start();
            System.out.println("wre");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //dont remove
        launch(args);

    }

    public static String userType(){
        System.out.println();
        if (m.isSignedIn()) {
            if (member!= null && member.isSignedIn()) {
                return "NonCommercial";
            }
            else if (admin !=null && admin.isSignedIn()) {
                return "Admin";
            }
        } else {

            return "User";
        }
        throw new RuntimeException("literally impossible");
    }

}
