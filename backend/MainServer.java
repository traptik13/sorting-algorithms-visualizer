import com.sun.net.httpserver.*;
import java.net.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public class MainServer {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8081"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("✅ Server running on port: " + port);

        // ✅ Root healthcheck endpoint for Railway
        server.createContext("/", exchange -> {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            String response = "✅ Backend server is running!";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.createContext("/signup", exchange -> {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                UserUtils.handleSignup(exchange);
            }
        });

        server.createContext("/login", exchange -> {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                UserUtils.handleLogin(exchange);
            }
        });

        server.createContext("/sort", exchange -> {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                SortingHandler.handleSort(exchange);
            }
        });

        server.createContext("/getHistory", exchange -> {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                UserUtils.handleGetHistory(exchange);
            }
        });

        server.createContext("/saveHistory", exchange -> {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                UserUtils.handleSaveHistory(exchange);
            }
        });

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static void addCORSHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
    }
}
