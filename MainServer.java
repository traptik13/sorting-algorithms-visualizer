import com.sun.net.httpserver.*;
import java.net.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public class MainServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

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
        

        // ✅ NEW CONTEXT FOR /saveHistory
        server.createContext("/saveHistory", exchange -> {
            addCORSHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                UserUtils.handleSaveHistory(exchange);
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("✅ Server running on http://localhost:8081");
    }

    private static void addCORSHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
    }
}
