/**
 * file: Message.java
 * @author John
 *
 */

/**
 * This class defines a generic message. It contains basic information
 * like the AS sending the message, the message type, etc.
 * 
 * Specific messages have to extend this class
 */
public abstract class Message {
	
	public static final int UPDATE_MSG = 0;
	public static final int SNAPSHOT_MSG = 1;
	public static final int FLOOD_MSG = 2;
	public static final int WITHDRAW_MSG = 4;
	public static final int CONTROL_MSG = 8;
	
	/** The AS originating the message */
	int asn;
	
	/** The message type */
	int messageType;
}
