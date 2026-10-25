package comp512st.paxos;

import java.util.*;

class Slots {
    private List<Integer> slotStore;
    private int nextFreeSlot;

    public Slots() {
        this.slotStore = new ArrayList<>();
        this.nextFreeSlot = 0;
    }

    public int getNextFreeSlot() {
        return nextFreeSlot;
    }

    public void addSlot(int slot) {
        slotStore.add(slot);
    }

    public void incrementNextFreeSlot() {
        nextFreeSlot++;
    }

}
