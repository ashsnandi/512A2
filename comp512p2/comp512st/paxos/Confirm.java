package comp512st.paxos;

import java.util.*;
import java.io.Serializable;

class Confirm implements Serializable {

    private final int slot;
    private final Object value;

    Confirm(int slot, Object val) {
        this.slot = slot;
        this.value=val;
    }
    public int getSlot(){
        return slot;
    }
    public Object getValue(){
        return value;
    }

}
