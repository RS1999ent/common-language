package simulator;
/**
 * file: ControlMessage.java
 * @author John
 *
 */

/**
 * This class defines a control message. This kind of message is used
 * to artificially instruct an AS to send out a withdrawal for a prefix
 * or an announcement. This is equivalent to a link going up or down
 */
public class ControlMessage extends Message {
	static final int ANNOUNCE = 0;
	static final int WITHDRAW = 1;
	
	int controlType;
	int announceTo;
	int dest;
	
	public ControlMessage(int asnum, int type, int to, int dst) {
		asn = asnum;
		messageType = CONTROL_MSG;
		controlType = type;
		announceTo = (int)to;
		dest = (int)dst;
	}
	
	public String toString() {
		return "From AS" + asn + " -> To AS" + announceTo + " " + (-2*controlType+1)*dest;
	}
}
