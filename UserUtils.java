import com.sun.net.httpserver.*;
import java.io.*;
import java.net.URLDecoder;
import java.sql.*;
import java.util.*;
import com.google.gson.*;

public class UserUtils {

    public static void handleSignup(HttpExchange exchange) throws IOException {
        enableCORS(exchange);
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Only POST allowed");
            return;
        }

        String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).lines()
                .reduce("", (acc, line) -> acc + line);
        JsonObject data = new Gson().fromJson(body, JsonObject.class);
        String username = data.get("username").getAsString();
        String password = data.get("password").getAsString();
        String email = data.get("email").getAsString();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                sendJson(exchange, Map.of("success", false, "message", "Username already exists"));
                return;
            }

            stmt = conn.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();

            sendJson(exchange, Map.of("success", true));
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, Map.of("success", false, "error", "Database error", "details", e.getMessage()));
        }
    }

    public static void handleLogin(HttpExchange exchange) throws IOException {
        enableCORS(exchange);
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Only POST allowed");
            return;
        }

        String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).lines()
                .reduce("", (acc, line) -> acc + line);
        JsonObject data = new Gson().fromJson(body, JsonObject.class);
        String username = data.get("username").getAsString();
        String password = data.get("password").getAsString();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username=? AND password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            boolean isValid = rs.next();
            sendJson(exchange, Map.of("success", isValid));
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, Map.of("success", false, "error", "Database error", "details", e.getMessage()));
        }
    }

    public static void handleSaveHistory(HttpExchange exchange) throws IOException {
        enableCORS(exchange);
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).lines()
                .reduce("", (acc, line) -> acc + line);
        JsonObject data = new Gson().fromJson(body, JsonObject.class);

        String username = data.get("username").getAsString();
        String algorithm = data.get("algorithm").getAsString();
        String inputArray = data.get("input_array").toString();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO history (username, algorithm, input_array) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, algorithm);
            stmt.setString(3, inputArray);
            stmt.executeUpdate();

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            sendJson(exchange, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Database error");
        }
    }

    public static void handleGetHistory(HttpExchange exchange) throws IOException {
        enableCORS(exchange);
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String username = null;
        if (query != null && query.startsWith("username=")) {
            username = URLDecoder.decode(query.split("=")[1], "UTF-8");
        }

        if (username == null || username.isEmpty()) {
            sendJson(exchange, List.of());
            return;
        }

        List<Map<String, String>> historyList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT algorithm, input_array, timestamp FROM history WHERE username = ? ORDER BY id DESC");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("algorithm", rs.getString("algorithm"));
                row.put("input_array", rs.getString("input_array"));
                row.put("timestamp", rs.getTimestamp("timestamp").toString());
                historyList.add(row);
            }

            sendJson(exchange, historyList);
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, List.of());
        }
    }

    private static void sendJson(HttpExchange exchange, Object data) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        headers.set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, json.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }
    }

    private static void sendResponse(HttpExchange exchange, int code, String msg) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(code, msg.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(msg.getBytes());
        }
    }

    private static void enableCORS(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
    }
}
