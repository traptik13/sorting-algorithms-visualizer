import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

public class SortingHandler {
    public static void handleSort(HttpExchange exchange) throws IOException {
        // Only set headers ONCE
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // No content
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(reader);
        StringBuilder buf = new StringBuilder();
        int b;
        while ((b = br.read()) != -1) buf.append((char) b);
        br.close();

        Gson gson = new Gson();
        JsonObject body = gson.fromJson(buf.toString(), JsonObject.class);
        JsonArray arr = body.getAsJsonArray("array");
        String algorithm = body.get("algorithm").getAsString();

        List<int[]> steps = new ArrayList<>();
        switch (algorithm) {
            case "Bubble Sort": SortingAlgorithms.bubbleSort(arr, steps); break;
            case "Insertion Sort": SortingAlgorithms.insertionSort(arr, steps); break;
            case "Selection Sort": SortingAlgorithms.selectionSort(arr, steps); break;
            case "Merge Sort": SortingAlgorithms.mergeSort(arr, 0, arr.size() - 1, steps); break;
            case "Quick Sort": SortingAlgorithms.quickSort(arr, 0, arr.size() - 1, steps); break;
            case "Heap Sort": SortingAlgorithms.heapSort(arr, steps); break;
            case "Shell Sort": SortingAlgorithms.shellSort(arr, steps); break;
            case "Radix Sort": SortingAlgorithms.radixSort(arr, steps); break;
        }

        String response = gson.toJson(Map.of("steps", steps));
        headers.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
