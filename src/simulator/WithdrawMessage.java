package simulator;
import integratedAdvertisement.IA;
import integratedAdvertisement.RootCause;

import java.util.ArrayList;

/**
 * file: WithdrawMessage.java
 * @author John
 *
 */

/**
 * This class extends the generic message class to define a 
 * withdrawal message. This essentially simplifies the
 * UpdateMessage by moving the withdrawal to a different message.
 * 
 * This is actually needed because withdrawals also have a root
 * cause.
 */
public class WithdrawMessage extends UWMessage{
	
	/**
	 * Constructor for a withdrawal message.
	 * @param asnum
	 * @param wp
	 * @param rc
	 */
	public WithdrawMessage(int asnum, ArrayList<Integer> wp, RootCause rc) {
		asn = asnum;
		messageType = WITHDRAW_MSG;
		asPath = new IA(rc);
		asPath.setPath_Legacy(null);
		prefixes = toArray(wp);
	}
	
	public String toString() {
		String msgStr = asn + ": ";
		msgStr += asPath.getRootCause() + ": -" + prefixes[0];
		return msgStr;
	}

}
