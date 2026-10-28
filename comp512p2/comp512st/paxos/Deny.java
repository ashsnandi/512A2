package comp512st.paxos;
import java.util.*;
import java.io.Serializable;

class Deny implements Serializable {
    private final Ballot ballot;
    private final int slot;
    private final Object value;

    public Deny(Ballot b, int slot, Object value) {
        this.ballot = b;
        this.slot = slot;
        this.value = value;
    }

    public Ballot getBallot(){
        return ballot;
    }

    public int getSlot(){
        return slot;
    }

    public Object getValue(){
        return value;
    }
}
