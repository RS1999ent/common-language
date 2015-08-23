/**
 * 
 */
package simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import integratedAdvertisement.IA;
import integratedAdvertisement.RootCause;

/**
 * @author David
 *
 */
public abstract class AS {

	static final int PROVIDER = 1;
	static final int PEER = 0;
	static final int CUSTOMER = -1;
	static final int SIBLING = 2;
	
		/** Set of neighbors that are customers */
	ArrayList<Integer> customers = new ArrayList<Integer>();

	/** Set of neighbors that are providers */
	ArrayList<Integer> providers = new ArrayList<Integer>();

	/** Set of neighbors that are peers */
	ArrayList<Integer> peers = new ArrayList<Integer>();
	
	public Integer asn;
	
	HashMap<Integer,IA> bestPath = new HashMap<Integer, IA>();

	public abstract boolean isBetter(IA p1, IA p2);

	public abstract int getNextHop(int dst);

	public abstract void floodCompleted(Set<RootCause> allIncomplete);

	public abstract void handleEvent(Event e);

	public abstract void announceSelf();

	public abstract void addCustomer(int as1);

	public abstract void addPeer(int as2);

	public abstract void addProvider(int as2);

	//gets all the paths for a particular destination (in the ribIn)
	public abstract Collection<IA> getAllPaths(int dst);

	//resets the as
	public abstract void RESET();


}
