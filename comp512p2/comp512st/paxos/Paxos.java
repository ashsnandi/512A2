package comp512st.paxos;

// Access to the GCL layer
import comp512.gcl.*;

import comp512.utils.*;

// Any other imports that you may need.
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;
import java.net.UnknownHostException;


// ANY OTHER classes, etc., that you add must be private to this package and not visible to the application layer.

// extend / implement whatever interface, etc. as required.
// NO OTHER public members / methods allowed. broadcastTOMsg, acceptTOMsg, and shutdownPaxos must be the only visible methods to the application layer.
//		You should also not change the signature of these methods (arguments and return value) other aspects maybe changed with reasonable design needs.
public class Paxos
{
	GCL gcl;
	FailCheck failCheck;
	private int seqNum = 0;
	private Thread acceptThread;
	private final java.util.concurrent.BlockingQueue<Object> delivered = new java.util.concurrent.LinkedBlockingQueue<>();
	Map<Integer, SlotState> slots = new HashMap<>();
	private int nextDeliver = 0;
	Attempt current = null;  
	private final int majority;
	private final int myId; // index in the group list, same on every process -> ballot tiebreak + Proposal origin

	public Paxos(String myProcess, String[] allGroupProcesses, Logger logger, FailCheck failCheck) throws IOException, UnknownHostException
	{
		// Rember to call the failCheck.checkFailure(..) with appropriate arguments throughout your Paxos code to force fail points if necessary.
		this.failCheck = failCheck;
		this.majority = (allGroupProcesses.length  / 2) + 1;
		this.myId = java.util.Arrays.asList(allGroupProcesses).indexOf(myProcess);

		// Initialize the GCL communication system as well as anything else you need to.
		this.gcl = new GCL(myProcess, allGroupProcesses, null, logger) ;

		// start last so the thread sees every field set
		this.acceptThread = new Thread(this::acceptLoop, "paxos-accept");
		this.acceptThread.setDaemon(true);
		this.acceptThread.start();
	}

	// Sole consumer of gcl.readGCMessage(): blocks until a message arrives, then dispatches it.
	private void acceptLoop()
	{
		try {
			while (!Thread.currentThread().isInterrupted()) { 
				GCMessage gcmsg = gcl.readGCMessage();
				Object msg = gcmsg.val;
				String sender = gcmsg.senderProcess;

				synchronized (this) {
					if (msg instanceof Propose) { // case propose
						Propose p = (Propose) msg;
						failCheck.checkFailure(FailCheck.FailureType.RECEIVEPROPOSE); // from spec
						SlotState st = slot(p.getSlot()); // get which slot happen sfrom thi smessage
						if (p.getBallotId().compareTo(st.promised) > 0) { // if this ballot is higher than the currently proposed one in the slot
							st.promised = p.getBallotId();
							gcl.sendMsg(new Promise(p.getBallotId(), p.getSlot(), st.acceptedBallot, st.acceptedValue), sender); // promise this new one 
						} else {
							// tell the proposer what we promised so it can jump past it
							gcl.sendMsg(new Deny(p.getBallotId(), p.getSlot(), st.promised), sender); // deny
						}
						failCheck.checkFailure(FailCheck.FailureType.AFTERSENDVOTE);
					} else if (msg instanceof Accept) {
						Accept a = (Accept) msg; // accep tmessage 
						SlotState st = slot(a.getSlot()); //get the slot
						if (a.getBallot().compareTo(st.promised) >= 0) { // if the acceting ballot is what im expecting it to be
							st.promised = a.getBallot();
							st.acceptedBallot = a.getBallot();
							st.acceptedValue = a.getValue();
							gcl.sendMsg(new AcceptAck(a.getBallot(), a.getSlot(), a.getValue()), sender);
						} else {
							gcl.sendMsg(new Deny(a.getBallot(), a.getSlot(), st.promised), sender);
						}
					} else if (msg instanceof Confirm) { //majority has been accepted
						Confirm c = (Confirm) msg;
						SlotState st = slot(c.getSlot());
						if (!st.decided) {
							st.decided = true;
							st.decidedValue = c.getValue();
						}
						// hand decided slots to the app strictly in slot order, stopping at the first gap
						while (slot(nextDeliver).decided) {
							delivered.put(((Proposal) slot(nextDeliver).decidedValue).getValue()); // unwrap for the app
							nextDeliver++;
						}

					} else if (msg instanceof Promise) {
						Promise p = (Promise) msg;
						if (isCurrent(p.getSlot(), p.getBallot())) {
							current.promises++;
							// must adopt the value accepted under the highest ballot, if any
							Ballot ab = p.getAcceptedBallot();
							if (ab != null && (current.highestSeen == null || ab.compareTo(current.highestSeen) > 0)) {
								current.highestSeen = ab;
								current.valueToPropose = (Proposal) p.getAcceptedValue();
							}
							notifyAll(); // neccesary because we're changing things
						}
					} else if (msg instanceof AcceptAck) {
						AcceptAck ack = (AcceptAck) msg;
						if (isCurrent(ack.getSlot(), ack.getBallot())) {
							current.accepts++;
							if (current.accepts == this.majority){ // == so we only confirm once
								gcl.broadcastMsg(new Confirm(ack.getSlot(), ack.getValue()));
							}
							notifyAll();
						}
					} else if (msg instanceof Deny) {
						Deny d = (Deny) msg;
						if (isCurrent(d.getSlot(), d.getBallot())) {
							current.deniedBy.add(sender); // a set, so one acceptor denying in both phases counts once
							Ballot higher = (Ballot) d.getValue();
							if (current.highestDenied == null || higher.compareTo(current.highestDenied) > 0) {
								current.highestDenied = higher;
							}
							notifyAll();
						}
					}
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	// state for slot s, created on first touch (caller holds the lock)
	private SlotState slot(int s)
	{
		return slots.computeIfAbsent(s, k -> new SlotState());
	}

	// true if a reply belongs to the proposal we're running right now (drops stale replies from old slots/ballots)
	private boolean isCurrent(int s, Ballot b)
	{
		return current != null && current.slot == s && current.ballot.compareTo(b) == 0;
	}

	// This is what the application layer is going to call to send a message/value, such as the player and the move
	public void broadcastTOMsg(Object val)
	{
		// This is just a place holder.
		// Extend this to build whatever Paxos logic you need to make sure the messaging system is total order.
		// Here you will have to ensure that the CALL BLOCKS, and is returned ONLY when a majority (and immediately upon majority) of processes have accepted the value.
		gcl.broadcastMsg(val);
	}

	// This is what the application layer is calling to figure out what is the next message in the total order.
	// Messages delivered in ALL the processes in the group should deliver this in the same order.
	public Object acceptTOMsg() throws InterruptedException
	{
		// Only decided values, in slot order, are put on this queue (by the accept thread).
		return delivered.take(); // blocks until we come to a consensus
	}

	// Add any of your own shutdown code into this method.
	public void shutdownPaxos()
	{
		acceptThread.interrupt();
		gcl.shutdownGCL();
	}
}


class Attempt {
    final int slot;
    final Ballot ballot;
    final Proposal myValue;
    int promises, accepts;
    final java.util.Set<String> deniedBy = new java.util.HashSet<>(); // acceptors that denied this ballot
    Proposal valueToPropose;
    Ballot highestSeen;     // highest acceptedBallot reported in a Promise
    Ballot highestDenied;   // highest ballot an acceptor denied us with

    Attempt(int slot, Ballot ballot, Proposal myValue) {
        this.slot = slot;
        this.ballot = ballot;
        this.myValue = myValue;
        this.valueToPropose = myValue;
    }

    // call right before sending Accept: phase-1 denies shouldn't count toward giving up in phase 2
    void startAcceptPhase() {
        deniedBy.clear();
    }

    // true if the value chosen for this slot is ours (compares ids, not references)
    boolean chosenIsMine() {
        return valueToPropose.sameAs(myValue);
    }
}
