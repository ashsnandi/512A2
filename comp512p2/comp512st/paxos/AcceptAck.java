package comp512st.paxos;

import java.util.*;
import java.io.Serializable;

public class AcceptAck implements Serializable {
    
    private final Ballot ballot;
    private final Object value;

    public AcceptAck(Ballot b, Object value) {
        this.ballot = b;
        this.value = value;
    }
    
}
