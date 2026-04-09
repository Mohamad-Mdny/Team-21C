package backend.APIs;

import backend.communication.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PaymentAPI {

    private final HttpServer server;


    public PaymentAPI(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/payment/store", new PaymentAPI.SavePaymentHandler());
        server.setExecutor(null);
    }


    public void start() {
        server.start();
        System.out.println("Payment API running at http://localhost:" + server.getAddress().getPort() + "/api/email/send");
    }

    public void stop(int delaySeconds) {
        server.stop(delaySeconds);
    }

    private static class SavePaymentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, jsonError("Method not allowed. Use POST."));
                return;
            }

            String body = readBody(exchange.getRequestBody());
            Map<String, String> payload = parseJsonFlat(body);

            String to = payload.get("to");
            String subject = payload.get("subject");
            String messageBody = payload.get("body");

            if (isBlank(to) || isBlank(subject) || isBlank(messageBody)) {
                sendJson(exchange, 400, jsonError("Missing required fields: to, subject, body"));
                return;
            }

            try {
                EmailSendResult result = SendGmail.sendGmail(to, subject, messageBody);

                String resp = "{"
                        + "\"success\":" + result.isSuccess() + ","
                        + "\"message\":" + jsonString(result.getMessage())
                        + "}";

                sendJson(exchange, 200, resp);

            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, jsonError("Server error: " + e.getMessage()));
            }
        }

        private static String readBody(InputStream in) throws IOException {
            byte[] bytes = in.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }

        /**
         * {"to":"x","subject":"y","body":"z"}
         */
        private static Map<String, String> parseJsonFlat(String json) {
            Map<String, String> map = new HashMap<>();
            if (json == null) return map;

            String s = json.trim();
            if (s.startsWith("{")) s = s.substring(1);
            if (s.endsWith("}")) s = s.substring(0, s.length() - 1);

            String[] parts = s.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            for (String part : parts) {
                String[] kv = part.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
                if (kv.length != 2) continue;

                String key = stripQuotes(kv[0].trim());
                String value = stripQuotes(kv[1].trim());

                map.put(key, value);
            }
            return map;
        }

        private static String stripQuotes(String s) {
            String out = s;
            if (out.startsWith("\"")) out = out.substring(1);
            if (out.endsWith("\"")) out = out.substring(0, out.length() - 1);
            out = out.replace("\\n", "\n").replace("\\\"", "\"");
            return out;
        }

        private static boolean isBlank(String s) {
            return s == null || s.trim().isEmpty();
        }

        private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private static String jsonError(String message) {
            return "{"
                    + "\"success\":false,"
                    + "\"message\":" + jsonString(message)
                    + "}";
        }

        private static String jsonString(String s) {
            if (s == null) return "\"\"";
            String esc = s.replace("\\", "\\\\").replace("\"", "\\\"");
            return "\"" + esc + "\"";
        }
    }
}
