package backend.APIs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PaymentAPITest {

    private PaymentAPI api;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        api = new PaymentAPI(18086);
        api.start();
        client = HttpClient.newHttpClient();
        baseUrl = "http://localhost:18086/api/payment/store";
    }

    @AfterEach
    void tearDown() {
        api.stop(0);
    }

    private HttpResponse<String> post(String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .GET()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    // Test 11.0: trying GET on a POST endpoint, should be 405
    @Test
    void getRequestReturns405() throws Exception {
        assertEquals(405, get().statusCode());
    }

    // Test 11.1: drop the amount field
    @Test
    void missingAmountReturns400() throws Exception {
        String json = "{\"billingAddress\":\"25 High Street\",\"cardNumber\":\"4111111111111111\",\"cvv\":\"123\",\"purchaseDate\":\"2026-04-11\",\"emailAddress\":\"cool@example.com\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.2: no billing address sent
    @Test
    void missingBillingAddressReturns400() throws Exception {
        String json = "{\"amount\":\"50\",\"cardNumber\":\"4111111111111111\",\"cvv\":\"123\",\"purchaseDate\":\"2026-04-11\",\"emailAddress\":\"cool@example.com\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.3: card number is missing
    @Test
    void missingCardNumberReturns400() throws Exception {
        String json = "{\"amount\":\"50\",\"billingAddress\":\"25 High Street\",\"cvv\":\"123\",\"purchaseDate\":\"2026-04-11\",\"emailAddress\":\"cool@example.com\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.4: cvv left out
    @Test
    void missingCvvReturns400() throws Exception {
        String json = "{\"amount\":\"50\",\"billingAddress\":\"25 High Street\",\"cardNumber\":\"4111111111111111\",\"purchaseDate\":\"2026-04-11\",\"emailAddress\":\"cool@example.com\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.5: amount is "abc" - not a number
    @Test
    void nonNumericAmountReturns400() throws Exception {
        String json = "{\"amount\":\"abc\",\"billingAddress\":\"25 High Street\",\"cardNumber\":\"4111111111111111\",\"cvv\":\"123\",\"purchaseDate\":\"2026-04-11\",\"emailAddress\":\"cool@example.com\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.6: amount = 0 should fail (must be > 0)
    @Test
    void zeroAmountReturns400() throws Exception {
        String json = "{\"amount\":\"0\",\"billingAddress\":\"25 High Street\",\"cardNumber\":\"4111111111111111\",\"cvv\":\"123\",\"purchaseDate\":\"2026-04-11\",\"emailAddress\":\"cool@example.com\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.7: negative amount, can't pay -5
    @Test
    void negativeAmountReturns400() throws Exception {
        String json = "{\"amount\":\"-5\",\"billingAddress\":\"25 High Street\",\"cardNumber\":\"4111111111111111\",\"cvv\":\"123\",\"purchaseDate\":\"2026-04-11\",\"emailAddress\":\"cool@example.com\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.8: empty json body
    @Test
    void emptyJsonReturns400() throws Exception {
        assertEquals(400, post("{}").statusCode());
    }

    // Test 11.9: no purchase date
    @Test
    void missingPurchaseDateReturns400() throws Exception {
        String json = "{\"amount\":\"50\",\"billingAddress\":\"25 High Street\",\"cardNumber\":\"4111111111111111\",\"cvv\":\"123\",\"emailAddress\":\"cool@example.com\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.10: no email
    @Test
    void missingEmailAddressReturns400() throws Exception {
        String json = "{\"amount\":\"50\",\"billingAddress\":\"25 High Street\",\"cardNumber\":\"4111111111111111\",\"cvv\":\"123\",\"purchaseDate\":\"2026-04-11\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 11.11: error body should say success:false
    @Test
    void errorResponseHasSuccessFalse() throws Exception {
        assertTrue(post("{}").body().contains("\"success\":false"));
    }
}
