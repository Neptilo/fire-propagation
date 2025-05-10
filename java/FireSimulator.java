import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
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
    private static int startingPointNum = 3;
    private static double propagationFactor = 0.5;
    private static int timeStepMs = 1000;

    // simulation state variables
    private static ArrayList<ArrayList<TileState>> map;
    private static LinkedList<IntPair> fireList;

    // state diff buffers
    private static LinkedList<IntPair> firePendingList;
    private static LinkedList<IntPair> ashPendingList;

    /* getters */

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    /* setters */

    public static void setWidth(int width) {
        FireSimulator.width = width;
    }

    public static void setHeight(int height) {
        FireSimulator.height = height;
    }

    public static void setStartingPointNum(int startingPointNum) {
        FireSimulator.startingPointNum = startingPointNum;
    }

    public static void setPropagationFactor(double propagationFactor) {
        FireSimulator.propagationFactor = propagationFactor;
    }

    public static void setTimeStepMs(int timeStepMs) {
        FireSimulator.timeStepMs = timeStepMs;
    }

    /* other methods */

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
        // initialize simulation state variables
        map = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            map.add(new ArrayList<>(Collections.nCopies(width, TileState.TREE)));
        }
        fireList = new LinkedList<>();
        firePendingList = new LinkedList<>();
        ashPendingList = new LinkedList<>();

        // start a fire:
        // randomly choose starting positions until we reach the desired number
        while (fireList.size() < startingPointNum) {
            int startRow = ThreadLocalRandom.current().nextInt(height);
            int startCol = ThreadLocalRandom.current().nextInt(width);

            // check that this position hasn't been chosen yet
            boolean alreadyChosen = false;
            for (IntPair pos : fireList) {
                if (pos.x() == startRow && pos.y() == startCol) {
                    alreadyChosen = true;
                    break;
                }
            }
            if (alreadyChosen)
                continue;

            IntPair startPos = new IntPair(startRow, startCol);
            map.get(startRow).set(startCol, TileState.FIRE);
            fireList.add(startPos);
            firePendingList.add(startPos);
        }

        // periodically increase FileServer.counter every second
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                () -> propagate(), 0, timeStepMs, TimeUnit.MILLISECONDS);
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

                // randomly decide if fire propagates to this tile
                if (ThreadLocalRandom.current().nextDouble() > propagationFactor)
                    continue; // That tree is safe for now.

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