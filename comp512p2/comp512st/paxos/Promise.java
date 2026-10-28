package comp512st.paxos;

import java.util.*;
import java.io.Serializable;

// reply to a Propose: "I promise ballot, and here is what I already accepted for this slot (null if nothing)"
class Promise implements Serializable {

    private final Ballot ballot;
    private final int slot;
    private final Ballot acceptedBallot;
    private final Object acceptedValue;

    public Promise(Ballot b, int slot, Ballot acceptedBallot, Object acceptedValue) {
        this.ballot = b;
        this.slot = slot;
        this.acceptedBallot = acceptedBallot;
        this.acceptedValue = acceptedValue;
    }

    public Ballot getBallot(){
        return ballot;
    }

    public int getSlot(){
        return slot;
    }

    public Ballot getAcceptedBallot(){
        return acceptedBallot;
    }

    public Object getAcceptedValue(){
        return acceptedValue;
    }
}
