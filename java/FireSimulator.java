import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FireSimulator {

    public static final FireSimulator instance = new FireSimulator();

    // simulation parameters
    private int width = 20;
    private int height = 20;
    private List<IntPair> propagationDirections = List.of(
            new IntPair(-1, 0),
            new IntPair(1, 0),
            new IntPair(0, -1),
            new IntPair(0, 1));
    private int startingPointNum = 3;
    private double propagationFactor = 0.5;
    private int timeStepMs = 1000;

    // simulation state variables
    private ArrayList<ArrayList<TileState>> map;
    private LinkedList<IntPair> fireList;

    private IFireObserver observer;

    private ScheduledExecutorService scheduler;

    private FireSimulator() {
    }

    /* getters */

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /* setters */

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setStartingPointNum(int startingPointNum) {
        this.startingPointNum = startingPointNum;
    }

    public void setPropagationFactor(double propagationFactor) {
        this.propagationFactor = propagationFactor;
    }

    public void setTimeStepMs(int timeStepMs) {
        this.timeStepMs = timeStepMs;
    }

    public void setObserver(IFireObserver observer) {
        this.observer = observer;
    }

    /* simulation logic */

    public void start() {
        // initialize simulation state variables
        map = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            map.add(new ArrayList<>(Collections.nCopies(width, TileState.TREE)));
        }
        fireList = new LinkedList<>();

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
        }
        if (observer != null)
            observer.onFireAdded(fireList);

        // periodically increase FileServer.counter every second
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (fireList.size() == 0) {
                scheduler.shutdown();
                System.out.println("Simulation ended");
            } else
                propagate();
        }, 0, timeStepMs, TimeUnit.MILLISECONDS);
        System.out.println("Simulation started");
    }

    public void propagate() {
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
        if (observer != null)
            observer.onAshAdded(fireList);
        fireList = newFireList;
        if (observer != null)
            observer.onFireAdded(fireList);
    }
}