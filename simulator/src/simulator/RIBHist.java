package simulator;
import integratedAdvertisement.IA;
import integratedAdvertisement.RootCause;

import java.util.*;

/**
 * file: RibHist.java
 * @author John
 */

/**
 * This class defines our new data structure RIB-Hist
 * This is similar to the RIB, but stores the history of
 * paths that I have accepted and sent out. The RIB-Hist
 * basically contains those paths which I can roll back to.
 * 
 * After syncing, I would choose the latest update on the
 * RIB-Hist that was completed, and chuck all older updates.
 * 
 * When a new best path is found, it is added to the RIB-Hist
 * and the RIB-Out. If it isn't sent to all its neighbors, 
 * it is evicted from the RIB-Hist and added to the set of
 * conditionally incomplete updates.
 */
public class RIBHist {
	/** The destination AS */
	int destAS; 
	
	ArrayList<IA> history = new ArrayList<IA>();

	ArrayList<UpdateDependency> condInUpdates = new ArrayList<UpdateDependency>();
	
	// This is required for implementing Tom's suggestion
	// Each update depends on all the previous updates I received
	// Thus, an update is complete only if it completes and all previous 
	// ones also complete
	ArrayList<RootCause> updateSequence = new ArrayList<RootCause>();
	
	// Stores the set of 'hot' neighbors. A neighbor is 'hot' if i have him
	// as my next hop in my history.
	HashSet<Integer> hotNeighbors = new HashSet<Integer>();
	
	public RIBHist(int dest) {
		destAS = dest;
	}
	
	// this is called whenever we advertise a path
	public void addUpdateToHistory(IA p, int nextHop) {
		history.add(p);
		// what if this is a withdrawal?
		hotNeighbors.add(nextHop);
	}
	
	public void addCondIncomplete(RootCause u, RootCause d) {
		if(u.equals(d))
			return;
		condInUpdates.add(new UpdateDependency(u,d));
	}
	
	/** 
	 * Removes the update with Path p from the history
	 * @param p
	 */
	public void removePath(IA p) {
		//TODO Need to check this
		history.remove(p);
	}
	
	public boolean isHotNbr(Integer nbr) {
		return hotNeighbors.contains(nbr);
	}

	/**
	 * This function removes the conditional dependency R1|R2 
	 * where R1 = rc. This is called when such an update is being 
	 * actively forwarded
	 * @param rc The dependency to remove
	 */
	public void removeConditional(RootCause rc) {
		for(int i=0; i<condInUpdates.size(); i++) {
			if(condInUpdates.get(i).update.equals(rc)) {
				condInUpdates.remove(i);
				i--;
			}
		}
	}

	/**
	 * This function is called whenever we receive or send out an update
	 * It captures the 'causality' between them
	 * @param rc
	 */
	public void addToSequence(RootCause rc) {
		// TODO Auto-generated method stub
		updateSequence.add(rc);
	}
}
