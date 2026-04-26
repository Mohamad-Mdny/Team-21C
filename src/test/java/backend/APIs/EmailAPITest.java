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

class EmailAPITest {

    private EmailAPI api;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        api = new EmailAPI(18085);
        api.start();
        client = HttpClient.newHttpClient();
        baseUrl = "http://localhost:18085/api/email/send";
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

    // Test 10.0: only POST allowed, GET should give 405
    @Test
    void getRequestReturns405() throws Exception {
        assertEquals(405, get().statusCode());
    }

    // Test 10.1: no 'to' field, expect 400
    @Test
    void missingToReturns400() throws Exception {
        String json = "{\"subject\":\"hello\",\"body\":\"text\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 10.2: no subject this time
    @Test
    void missingSubjectReturns400() throws Exception {
        String json = "{\"to\":\"cool@example.com\",\"body\":\"text\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 10.3: body missing - shouldn't go through
    @Test
    void missingBodyReturns400() throws Exception {
        String json = "{\"to\":\"cool@example.com\",\"subject\":\"hello\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 10.4: everything blank, all empty strings
    @Test
    void allBlankFieldsReturns400() throws Exception {
        String json = "{\"to\":\"\",\"subject\":\"\",\"body\":\"\"}";
        assertEquals(400, post(json).statusCode());
    }

    // Test 10.5: just an empty json object
    @Test
    void emptyJsonReturns400() throws Exception {
        assertEquals(400, post("{}").statusCode());
    }

    // Test 10.6: check the error JSON has success:false in it
    @Test
    void errorResponseHasSuccessFalse() throws Exception {
        assertTrue(post("{}").body().contains("\"success\":false"));
    }
}
