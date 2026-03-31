package backend;

import backend.communication.*;
import backend.models.Member;
import backend.models.User;
import com.almasb.fxgl.notification.NotificationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;


import java.io.IOException;

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
        //dont remove
        launch(args);

        //EmailSendResult result = SendGmail.sendGmail("surya.premkumar@city.ac.uk", "Test 123", "Hello world java");
    }

}
