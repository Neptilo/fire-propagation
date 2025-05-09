import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FireSimulator {

    // simulation parameters
    private static int width = 20;
    private static int height = 20;
    private static List<IntPair> propagationDirections = List.of(
            new IntPair(-1, 0),
            new IntPair(1, 0),
            new IntPair(0, -1),
            new IntPair(0, 1));

    // simulation state variables
    private static ArrayList<ArrayList<TileState>> map;
    private static LinkedList<IntPair> fireList;

    // state diff buffers
    private static LinkedList<IntPair> firePendingList;
    private static LinkedList<IntPair> ashPendingList;

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static LinkedList<IntPair> popFireList() {
        LinkedList<IntPair> res = firePendingList;
        firePendingList = new LinkedList<>();
        return res;
    }  
    
    public static LinkedList<IntPair> popAshList() {
        LinkedList<IntPair> res = ashPendingList;
        ashPendingList = new LinkedList<>();
        return res;
    }

    public static void start() {
        map = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            map.add(new ArrayList<>(Collections.nCopies(width, TileState.TREE)));
        }
        fireList = new LinkedList<>();
        firePendingList = new LinkedList<>();
        ashPendingList = new LinkedList<>();

        // start a fire
        map.get(0).set(0, TileState.FIRE);
        fireList.add(new IntPair(0, 0));
        firePendingList.add(new IntPair(0, 0));

        // periodically increase FileServer.counter every second
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                () -> propagate(), 0, 1000, TimeUnit.MILLISECONDS);
    }

    public static void propagate() {
        LinkedList<IntPair> newFireList = new LinkedList<>();
        for (IntPair tilePos : fireList) {
            // set new tiles on fire
            for (IntPair propagationDir : propagationDirections) {
                int row = tilePos.x() + propagationDir.x();
                int col = tilePos.y() + propagationDir.y();
                if (row < 0 || col < 0 || row >= height || col >= width)
                    continue;

                TileState state = map.get(row).get(col);
                if (state != TileState.TREE)
                    continue;

                newFireList.add(new IntPair(row, col));
                map.get(row).set(col, TileState.FIRE);
            }

            // extinguish old fire tiles
            map.get(tilePos.x()).set(tilePos.y(), TileState.ASH);
        }

        // update data state lists
        ashPendingList.addAll(fireList);
        fireList = newFireList;
        firePendingList.addAll(fireList);
    }
}