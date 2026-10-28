package comp512st.paxos;

import java.io.Serializable;

// wraps an app value with a unique id so we can tell values apart after they've been serialized
class Proposal implements Serializable {

    private final int origin; // process that first proposed it
    private final int seq;    // per-process counter
    private final Object value;

    Proposal(int origin, int seq, Object value) {
        this.origin = origin;
        this.seq = seq;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    // same logical proposal, even if one side is a deserialized copy
    boolean sameAs(Proposal other) {
        return other != null && origin == other.origin && seq == other.seq;
    }

}
