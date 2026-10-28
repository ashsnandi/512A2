package comp512st.paxos;

import java.util.*;
import java.io.Serializable;

public class Ballot implements Serializable {

    private final int round;
    private final int processId;

    Ballot(int round, int processId) {
        this.round = round;
        this.processId = processId;
    }

    int getRound() {
        return round;
    }

    public List<Integer> getUniqueBallotId() {

        // eg for player 1, round 1, the ballot id is (1, 1)
        return List.of(round, processId);
    }

    int compareTo(Ballot other) {
        int roundComparison = Integer.compare(this.round, other.round);
        if (roundComparison != 0) {
            // this round is higher
            return roundComparison;
        }
        return Integer.compare(this.processId, other.processId);
    }

}
