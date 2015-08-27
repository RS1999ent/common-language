package simulator;
/**
 * file: SnapshotMessage.java
 * @author John
 *
 */

/**
 * This class extends the generic Message class to define a Snapshot Message
 */
public class SnapshotMessage extends Message {
	
	public SnapshotMessage(int asnum) {
		asn = asnum;
		messageType = Message.SNAPSHOT_MSG;
	}
}
