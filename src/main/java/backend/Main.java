package backend;

import backend.APIs.*;
import backend.models.Admin;
import backend.models.Member;
import backend.models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {
    public static double VAT_RATE = 0.00;
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

    public static void main(String[] args) {
        try {
            new backend.APIs.EmailAPI(8085).start();
            new PaymentAPI(8086).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //dont remove
        launch(args);

    }

    public static String userType() {
        System.out.println();
        if (m.isSignedIn()) {
            if (member != null && member.isSignedIn()) {
                return "NonCommercial";
            } else if (admin != null && admin.isSignedIn()) {
                return "Admin";
            }
        } else {

            return "User";
        }
        throw new RuntimeException("literally impossible");
    }

}
