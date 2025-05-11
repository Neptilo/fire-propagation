import java.util.LinkedList;

/**
 * Interface for an object listening to FireSimulator's cell changes
 */
public interface IFireObserver {
    abstract void onFireAdded(LinkedList<IntPair> list);
    abstract void onAshAdded(LinkedList<IntPair> list);
}
