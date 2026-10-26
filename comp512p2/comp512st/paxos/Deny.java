package comp512st.paxos;
import java.util.*;
import java.io.Serializable;

public class Deny implements Serializable {
    private final Ballot ballot;
    private final Object value;

    public Deny(Ballot b, Object value) {
        this.ballot = b;
        this.value = value;
    }
}
