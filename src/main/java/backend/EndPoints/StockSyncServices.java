package backend.EndPoints;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// the endpoint provided by Team B

public class StockSyncServices {

    private static final String CA_API_BASE = "http://localhost:4567/api";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();


    public static List<ProductData> searchProducts(String query) {
        List<ProductData> results = new ArrayList<>();
        try {
            String url = CA_API_BASE + "/products/search?query=" + java.net.URLEncoder.encode(query, "UTF-8");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObj = jsonArray.get(i).getAsJsonObject();
                    results.add(new ProductData(
                            jsonObj.get("id").getAsInt(),
                            jsonObj.get("name").getAsString(),
                            jsonObj.get("price").getAsDouble(),
                            jsonObj.get("stock").getAsInt()
                    ));
                }
            } else {
                System.err.println("Search failed: HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
        return results;
    }

    public static ProductData getProductDetails(int productId) {
        try {
            String url = CA_API_BASE + "/products/" + productId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonObj = gson.fromJson(response.body(), JsonObject.class);
                return new ProductData(
                        jsonObj.get("id").getAsInt(),
                        jsonObj.get("name").getAsString(),
                        jsonObj.get("price").getAsDouble(),
                        jsonObj.get("stock").getAsInt()
                );
            }
        } catch (Exception e) {
            System.err.println("Error fetching product details: " + e.getMessage());
        }
        return null;
    }

    public static boolean isStockAvailable(int productId, int quantity) {
        try {
            String url = CA_API_BASE + "/inventory/stock/" + productId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject result = gson.fromJson(response.body(), JsonObject.class);
                int currentStock = result.get("stockLevel").getAsInt();
                return currentStock >= quantity;
            }
        } catch (Exception e) {
            System.err.println("Error checking stock: " + e.getMessage());
        }
        return false; // Fail safe: assume out of stock on error
    }

    public static boolean decrementStockInCA(int productId, int quantity, String saleId) {
        try {
            Map<String, Object> bodyData = Map.of(
                    "productId", productId,
                    "quantity", quantity,
                    "saleId", saleId,
                    "reason", "Online Sale (IPOS-PU)"
            );
            String jsonBody = gson.toJson(bodyData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CA_API_BASE + "/inventory/decrement"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject result = gson.fromJson(response.body(), JsonObject.class);
                if (result.has("success") && result.get("success").getAsBoolean()) {
                    System.out.println("CA confirmed stock update for product " + productId);
                    return true;
                } else {
                    String errorMsg = result.has("error") ? result.get("error").getAsString() : "Unknown error";
                    System.err.println("CA rejected: " + errorMsg);
                    return false;
                }
            } else if (response.statusCode() == 409) {
                JsonObject result = gson.fromJson(response.body(), JsonObject.class);
                String errorMsg = result.has("error") ? result.get("error").getAsString() : "Out of Stock";
                System.err.println("Out of Stock in CA: " + errorMsg);
                return false;
            } else {
                System.err.println("CA API Error: HTTP " + response.statusCode());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to connect to IPOS-CA. Is it running?");
            e.printStackTrace();
            return false;
        }
    }

    public static class ProductData {
        public final int id;
        public final String name;
        public final double price;
        public final int stock;

        public ProductData(int id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return name + " (Â£" + price + ") - Stock: " + stock;
        }
    }
}