package simulator;
import integratedAdvertisement.RootCause;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * file: FloodMessage.java
 * @author John
 *
 */

/**
 * This class defines the message used for flooding the
 * incomplete-update state information to all other nodes
 */
public class FloodMessage extends Message {
	HashSet<RootCause> incompleteUpdates = new HashSet<RootCause>();
	HashSet<UpdateDependency> condIncompleteUpdates = new HashSet<UpdateDependency>();
	
	public FloodMessage(int asnum, ArrayList<RootCause> incompUp, ArrayList<UpdateDependency> condIncomp) {
		asn = asnum;
		messageType = FLOOD_MSG;
		// TODO may need to copy the updates
		// TODO may need to add epoch numbers for snapshot and flood messages
		incompleteUpdates.addAll(incompUp);
		condIncompleteUpdates.addAll(condIncomp);
	}
	
	public String toString() {
		return "Incomplete: " + incompleteUpdates + "\nConditional: " + condIncompleteUpdates;
	}
}
