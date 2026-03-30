package backend;

import backend.communication.*;
import com.almasb.fxgl.notification.NotificationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.IOException;

public class Main extends Application {
    public static User m = new User();
    @Override

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/frontend/Catalogue.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Stuff");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String email = System.getenv("EMAIL");
        String password = System.getenv("EMAIL_PASSWORD");

        EmailProvider provider = new GmailSmtpEmailProvider(email, password);
        INotificationService service = new NotificationServiceImpl(provider);

        EmailSendResult result = service.sendEmail("email@gmail.com", "subject", "Email message");
    }

}
