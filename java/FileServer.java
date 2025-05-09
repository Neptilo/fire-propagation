import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

public class FileServer {

    public static void start(int port, Path rootDir) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

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

        server.createContext("/api/map-size", exchange -> {
            String response = "{" +
                    "\"width\": " + FireSimulator.getWidth() +
                    ",\"height\": " + FireSimulator.getHeight() +
                    "}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.createContext("/api/diff", exchange -> {
            StringJoiner joiner = new StringJoiner(",", "[", "]");
            for (IntPair tilePos : FireSimulator.popFireList()) {
                joiner.add("[" + tilePos.x() + "," + tilePos.y() + "]");
            }
            String response = joiner.toString();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        server.setExecutor(null); // default executor
        server.start();
        System.out.println("Server running on http://localhost:" + port);
    }
}
