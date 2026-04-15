package backend.APIs;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class EmailEndpoint {

    public EmailEndpoint(String json) throws IOException, InterruptedException {
        /**
            String json = """
            {
              "to": "estroyer221@gmail.com",
              "subject": "IPOS-PU",
              "body": "Test IPOS-PU",
            }
            """;
         */
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8085/api/email/send"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    }
}
