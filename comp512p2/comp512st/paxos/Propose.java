package comp512st.paxos;

import java.io.Serializable;

class Propose implements Serializable {

    private final int ballotId;
    private final int slot;

    public Propose(int ballotId, int slot) {
        this.ballotId = ballotId;
        this.slot = slot;
    }

    public int getBallotId() {
        return ballotId;
    }

    public int getSlot() {
        return slot;
    }

}
