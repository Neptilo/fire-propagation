import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.StringJoiner;

/**
 * A static class to define and handle a server for front-end clients to
 * connect to, to interact with the fire simulation running in the back-end
 */
public class FileServer {

    private static HttpServer server;

    /**
     * Start the server
     * @param port The port on which the server is started
     * @param rootDir The relative path of the directory where the static
     *                web files are located
     * @throws IOException
     */
    public static void start(int port, Path rootDir) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            Path filePath = rootDir.resolve(path.substring(1)).normalize();

            if (!filePath.startsWith(rootDir)) {
                System.out.println("Path is outside of the root directory!");
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            if (!Files.exists(filePath)) {
                System.out.println("File does not exist: " + filePath);
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            if (Files.isDirectory(filePath)) {
                System.out.println("Requested path is a directory, not a file: " + filePath);
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType != null) {
                exchange.getResponseHeaders().add("Content-Type", contentType);
            }

            byte[] bytes = Files.readAllBytes(filePath);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        // instantiate a buffer listening to the changes in the fire simulation
        FireSimulator simulator = FireSimulator.instance;
        FireChangeCache cache = new FireChangeCache();

        // define route to fetch the initial simulation data (= the size)
        server.createContext("/api/map-size", exchange -> {
            String response = "{" +
                    "\"width\": " + simulator.getWidth() +
                    ",\"height\": " + simulator.getHeight() +
                    "}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        // define route to request (POST) the simulation start
        // Doesn't expect a response.
        server.createContext("/api/start", exchange -> {
            simulator.start();
            simulator.setObserver(cache);
        });

        // define a route to get the cell changes since the last update
        server.createContext("/api/diff", exchange -> {
            String response = "{" +
                "\"fire\": " + stringifyIntPairList(cache.popFireList()) +
                ",\"ash\": " + stringifyIntPairList(cache.popAshList()) +
                "}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.start();
        System.out.println("Server running on http://localhost:" + port);
    }

    /**
     * Stop the server
     */
    public static void stop() {
        server.stop(0);
    }

    /**
     * Convert an IntPair list to JSON format
     * @param list The input list
     * @return A JSON-formatted string
     */
    private static String stringifyIntPairList(LinkedList<IntPair> list) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (IntPair tilePos : list) {
            joiner.add("[" + tilePos.x() + "," + tilePos.y() + "]");
        }
        return joiner.toString();
    }
}
