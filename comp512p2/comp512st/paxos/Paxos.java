package comp512st.paxos;

// Access to the GCL layer
import comp512.gcl.*;

import comp512.utils.*;

// Any other imports that you may need.
import java.io.*;
import java.util.logging.*;
import java.net.UnknownHostException;
import java.util.*;
// ANY OTHER classes, etc., that you add must be private to this package and not visible to the application layer.

// extend / implement whatever interface, etc. as required.
// NO OTHER public members / methods allowed. broadcastTOMsg, acceptTOMsg, and shutdownPaxos must be the only visible methods to the application layer.
//		You should also not change the signature of these methods (arguments and return value) other aspects maybe changed with reasonable design needs.
public class Paxos {
	GCL gcl;
	FailCheck failCheck;

	private int myProcessId;
	private String[] allGroupProcesses;
	private int round = 0;

	private Logger logger;

	// Slots for the total order of sequencing
	private final Slots slots = new Slots();

	public Paxos(String myProcess, String[] allGroupProcesses, Logger logger, FailCheck failCheck)
			throws IOException, UnknownHostException {
		// Rember to call the failCheck.checkFailure(..) with appropriate arguments
		// throughout your Paxos code to force fail points if necessary.
		this.failCheck = failCheck;

		// Initialize the GCL communication system as well as anything else you need to.
		this.gcl = new GCL(myProcess, allGroupProcesses, null, logger);

		// intialise the necessary variables
		String[] sortedProcesses = Arrays.copyOf(allGroupProcesses, allGroupProcesses.length);
		Arrays.sort(sortedProcesses);
		this.myProcessId = Arrays.asList(sortedProcesses).indexOf(myProcess);
		this.allGroupProcesses = sortedProcesses;

		this.logger = logger;
	}

	// This is what the application layer is going to call to send a message/value,
	// such as the player and the move
	public void broadcastTOMsg(Object val) {

		logger.info("myProcess: " + myProcessId);
		logger.info("allGroupProcesses: " + Arrays.toString(allGroupProcesses));
		// ---------------------
		// Default text from base assignment
		// ---------------------
		// This is just a place holder.
		// Extend this to build whatever Paxos logic you need to make sure the messaging
		// system is total order.
		// Here you will have to ensure that the CALL BLOCKS, and is returned ONLY when
		// a majority (and immediately upon majority) of processes have accepted the
		// value.
		// gcl.broadcastMsg(val);

		// ---------------------
		// IMPLEMENTATION
		// ---------------------

		// 1) choose a slot
		int slot = slots.getNextFreeSlot();
		slots.incrementNextFreeSlot();

		// choose ballotId
		round++;
		Ballot bId = new Ballot(round, myProcessId);

		// send propose(ballotId, val)
		gcl.broadcastMsg(new Object[] { "propose", bId, slot });

		// finish after accept can return promises..
		// boolean recievedPromises = false;
		// while (!recievedPromises) {

		// wait for promise(ballotId)

	}

	// This is what the application layer is calling to figure out what is the next
	// message in the total order.
	// Messages delivered in ALL the processes in the group should deliver this in
	// the same order.
	public Object acceptTOMsg() throws InterruptedException {
		// This is just a place holder.
		GCMessage gcmsg = gcl.readGCMessage();
		return gcmsg.val;
	}

	// Add any of your own shutdown code into this method.
	public void shutdownPaxos() {
		gcl.shutdownGCL();
	}
}
