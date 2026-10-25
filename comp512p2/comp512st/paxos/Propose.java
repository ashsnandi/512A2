package comp512st.paxos;

import java.io.Serializable;

class Propose implements Serializable {

    private final Ballot ballotId;
    private final int slot;

    public Propose(Ballot ballotId, int slot) {
        this.ballotId = ballotId;
        this.slot = slot;
    }

    public Ballot getBallotId() {
        return ballotId;
    }

    public int getSlot() {
        return slot;
    }

}
