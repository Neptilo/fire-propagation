import java.nio.file.Path;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        ConfigLoader.load();
        FireSimulator.start();

        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fire propagation");

        WebView webView = new WebView();

        Path webRoot = Path.of("web");
        try {
            FileServer.start(8080, webRoot);
        } catch (Exception e) {
            System.out.println("Couldn't start server");
            e.printStackTrace();
            return;
        }

        webView.getEngine().load("http://localhost:8080/index.html");

        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 600, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}