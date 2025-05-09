import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collections;

public class FireSimulator {

    private static int width = 20;
    private static int height = 20;

    private static ArrayList<ArrayList<Tile>> map;

    public static void start() {
        map = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            map.add(new ArrayList<>(Collections.nCopies(width, Tile.TREE)));
        }

        // start a fire
        map.get(0).set(0, Tile.FIRE);

        // periodically increase FileServer.counter every second
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }
}