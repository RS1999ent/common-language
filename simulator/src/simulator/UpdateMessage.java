package simulator;
import integratedAdvertisement.IA;

import java.util.*;

/**
 * file: UpdateMessage.java
 * @author John
 *
 */

/**
 * This class describes a message (like an update message)
 * As of now, it just supports the update message, but if
 * necessary, we will make this an abstract class, and
 * have the different types of messages extend it.
 * 
 */
public class UpdateMessage extends UWMessage{
	
	/**
	 * Constructor for a message object
	 * 
	 * @param asnum The AS sending the message
	 * @param ap The set of prefixes announced
	 * @param asp The AS-Path of the announced prefixes
	 */
	public UpdateMessage( int asnum, ArrayList<Integer> ap, IA asp ) {
		asn = asnum;
		messageType = Message.UPDATE_MSG;
		
		prefixes = toArray(ap);
		
		//asPath = new IA(asp.getPath(), asp.getRootCause());
		asPath = new IA(asp);
	}
	
	// bad programming practice!
	public String toString() {
		String msgStr = asn + ": ";
		msgStr += asPath.getRootCause() + ": " + asPath.getPath();
//		msgStr += asPath.rc + ": " ;
//		// this hack is required because a loopy update
//		// message is to be treated as a withdrawal
//		if(asPath.path==null) {
//			msgStr += "-" + prefixes[0]; 
//		}
//		else {
//			msgStr += asPath.path;
//		}
		return msgStr;
	}
}
