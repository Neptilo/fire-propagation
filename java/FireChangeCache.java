import java.util.LinkedList;

public class FireChangeCache implements IFireObserver {

    // state diff buffers
    private LinkedList<IntPair> firePendingList;
    private LinkedList<IntPair> ashPendingList;
    
    FireChangeCache() {
        firePendingList = new LinkedList<>();
        ashPendingList = new LinkedList<>();
    }

    @Override
    public void onFireAdded(LinkedList<IntPair> list) {
        firePendingList.clear();
        firePendingList.addAll(list);
    }

    @Override
    public void onAshAdded(LinkedList<IntPair> list) {
        ashPendingList.addAll(list);
    }
    
    public LinkedList<IntPair> popFireList() {
        LinkedList<IntPair> res = firePendingList;
        firePendingList = new LinkedList<>();
        return res;
    }

    public LinkedList<IntPair> popAshList() {
        LinkedList<IntPair> res = ashPendingList;
        ashPendingList = new LinkedList<>();
        return res;
    }
}
