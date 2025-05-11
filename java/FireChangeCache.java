import java.util.LinkedList;

/**
 * A buffer to store pending changes of cell states
 */
public class FireChangeCache implements IFireObserver {

    // state diff buffers
    private LinkedList<IntPair> firePendingList;
    private LinkedList<IntPair> ashPendingList;
    
    FireChangeCache() {
        firePendingList = new LinkedList<>();
        ashPendingList = new LinkedList<>();
    }

    /**
     * Update the fire cell list stored in this buffer
     * @param list List of *all* the current fire cells
     */
    @Override
    public void onFireAdded(LinkedList<IntPair> list) {
        firePendingList.clear();
        firePendingList.addAll(list);
    }

    /**
     * Update the ash cell list stored in this buffer
     * @param list List of *newly added* ash cells
     */
    @Override
    public void onAshAdded(LinkedList<IntPair> list) {
        ashPendingList.addAll(list);
    }
    
    /**
     * Retrieve and clear the list of pending fire cells
     * @return The list of pending fire cells
     */
    public LinkedList<IntPair> popFireList() {
        LinkedList<IntPair> res = firePendingList;
        firePendingList = new LinkedList<>();
        return res;
    }

    /**
     * Retrieve and clear the list of pending ash cells
     * @return The list of pending ash cells
     */
    public LinkedList<IntPair> popAshList() {
        LinkedList<IntPair> res = ashPendingList;
        ashPendingList = new LinkedList<>();
        return res;
    }
}
