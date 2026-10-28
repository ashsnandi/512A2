package comp512st.paxos;

import java.util.*;

class SlotState {
    Ballot promised = new Ballot(-1,-1);   // maxBallotID, per slot
    Ballot acceptedBallot;                  // null until accepted
    Object acceptedValue;
    boolean decided;
    Object decidedValue;
}
