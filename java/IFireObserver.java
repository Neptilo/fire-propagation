import java.util.LinkedList;

public interface IFireObserver {
    abstract void onFireAdded(LinkedList<IntPair> list);
    abstract void onAshAdded(LinkedList<IntPair> list);
}
