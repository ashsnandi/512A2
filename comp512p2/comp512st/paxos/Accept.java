package comp512st.paxos;

import java.util.*;
import java.io.Serializable;

public class Accept implements Serializable {
    
    private final Ballot ballot;
    private final Object value;
    
    public Accept(Ballot b, Object value) {
        this.ballot = b;
        this.value = value;
    }

    
}
