package backend.APIs;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class PaymentEndpoint {

    public PaymentEndpoint(String json) throws IOException, InterruptedException {

        /**
         String json = """
             {
             "amount": 120.50,
             "billingAddress": "1647 Testing Address",
             "cardNumber": "4111111111111111",
             "cvv": "123",
             "purchaseDate": "2026-04-11T12:34:56",
             "emailAddress": "demo.member@ipos.com"
             }
         """
         */

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8086/api/payment/store"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    }
}
