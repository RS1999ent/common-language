/**
 * 
 */
package simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import simulator.AS.Node;
import integratedAdvertisement.IA;
import integratedAdvertisement.PassThrough;
import integratedAdvertisement.RootCause;

/**
 * 
 * @author David
 *
 */
public abstract class AS {

	static final int PROVIDER = 1;
	static final int PEER = 0;
	static final int CUSTOMER = -1;
	static final int SIBLING = 2;
	
	static final int BGP = 500;
	static final int WISER = 501;
	static final int TRANSIT = 502;
	
		/** Set of neighbors that are customers */
	ArrayList<Integer> customers = new ArrayList<Integer>();

	/** Set of neighbors that are providers */
	ArrayList<Integer> providers = new ArrayList<Integer>();

	/** Set of neighbors that are peers */
	ArrayList<Integer> peers = new ArrayList<Integer>();
	
	// class to hold a point of presence tuple to key on
	/**
	 * class to hold tuple of points of presence pairs
	 * @author David
	 *
	 */
	public static class PoPTuple {
		
		@Override
		public String toString() {
			return "PoPTuple [pop1=" + pop1 + ", pop2=" + pop2 + "]";
		}

		public Integer pop1;
		public Integer pop2;
		
		public PoPTuple(int pop1, int pop2)
		{
			this.pop1 = pop1;
			this.pop2 = pop2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pop1 == null) ? 0 : pop1.hashCode());
			result = prime * result + ((pop2 == null) ? 0 : pop2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PoPTuple other = (PoPTuple) obj;
			if (pop1 == null) {
				if (other.pop1 != null)
					return false;
			} else if (!pop1.equals(other.pop1))
				return false;
			if (pop2 == null) {
				if (other.pop2 != null)
					return false;
			} else if (!pop2.equals(other.pop2))
				return false;
			return true;
		}
		
	
	}

	// hashmap to find latencies. neighbor (int) -> hashmap of point of presense
	// tuples -> latency
	HashMap<Integer, HashMap<PoPTuple, Integer>> neighborLatency = new HashMap<Integer, HashMap<PoPTuple, Integer>>();

	// adjacency list for intradomain pop adjacencies. Goes Pop -> hash adjacentpop -> latency
	HashMap<Integer, HashMap<Integer, Integer>> intraD = new HashMap<Integer, HashMap<Integer, Integer>>();
	
	PassThrough passThrough = new PassThrough(); //enable passthroughfunctionality for AS
	
	/** Mapping of neighbor to relationship */
	HashMap<Integer, Integer> neighborMap = new HashMap<Integer, Integer>();

	// we also need to store all the paths received from neighbors for each
	// destination. this would be our rib-in. the rib-in is implemented as
	// a pair of nested hash tables: hashed on <prefix, neighbor>
	HashMap<Integer, HashMap<Integer,IA>> ribIn = new HashMap<Integer, HashMap<Integer, IA>>();

	
	public Integer asn;
	
	public Integer protocol;
	
	HashMap<Integer,IA> bestPath = new HashMap<Integer, IA>();

	public abstract boolean isBetter(IA p1, IA p2);

	public abstract int getNextHop(int dst);

	public abstract void floodCompleted(Set<RootCause> allIncomplete);

	public abstract void handleEvent(Event e);

	public abstract void announceSelf();

	public abstract void addCustomer(int as1);

	public abstract void addPeer(int as2);

	public abstract void addProvider(int as2);
	
	/**
	 * adds the latency between to ponts of presence between neigbhroing ases
	 * 
	 * @param as
	 *            the neighbor AS
	 * @param popPair
	 *            the points of presence connecting the two
	 * @param latency
	 *            the latency between those points of presence
	 */
	public void addLatency(int as, PoPTuple popPair, int latency) {
		// grab reference to the has table if it exists for this neighbor
		HashMap<PoPTuple, Integer> temp;
		if (!neighborLatency.containsKey(as)) {
			temp = new HashMap<PoPTuple, Integer>();
			neighborLatency.put(as, temp);
		}
		temp = neighborLatency.get(as);
		// add the latency for the pair
		temp.put(popPair, latency);

	}
	
	/**
	 * function that adds an intradomain adjacency between two poitns of
	 * presence
	 * 
	 * @param pop1
	 *            one point of presence
	 * @param pop2
	 *            another point of presence
	 * @param latency
	 *            the latency between the two
	 */
	public void addIntraDomainLatency(int pop1, int pop2, int latency) {
		// grab adjacency hashMaps from table (if they don't exist, make them)
		HashMap<Integer, Integer> pop1Adjacency, pop2Adjacency;
		if (!intraD.containsKey(pop1)) {
			pop1Adjacency = new HashMap<Integer, Integer>();
			intraD.put(pop1, pop1Adjacency);
		}
		if (!intraD.containsKey(pop2)) {
			pop2Adjacency = new HashMap<Integer, Integer>();
			intraD.put(pop2, pop2Adjacency);
		}
		pop1Adjacency = intraD.get(pop1);
		pop2Adjacency = intraD.get(pop2);

		// add the latencies to the adjacent pairs
		pop1Adjacency.put(pop2, latency);
		pop2Adjacency.put(pop1, latency);

	}
	
	class Node implements Comparator<Node>
	{
		
		public int node;
		public int distance;
		Node(int node, int distance)
		{
			this.node = node;
			this.distance = distance;
		}
		
		public Node() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public int compare(Node arg0, Node arg1) {
			if(arg0.distance < arg1.distance)
			{
				return -1;
			}
			if(arg0.distance > arg1.distance){
				return 1;
			}
			else{
				return 0;
			}
		}
		
	}

	private Integer getLowestCost(HashSet<Integer> nodes, HashMap<Integer, Integer> distance)
	{
		Integer lowestCost = Integer.MAX_VALUE;
		Integer lowKey = null;
		for(Iterator<Integer> it = nodes.iterator(); it.hasNext();)
		{
			int node = it.next();
			if(distance.get(node) < lowestCost)
			{
				lowKey = node;
			}
		}
		return lowKey;
		
	}
	
	
	HashMap<PoPTuple, Integer> intraDomainLatencies = new HashMap<PoPTuple, Integer>();
	/**
	 * precomutes intradomain costs between all pairs of points of presence
	 */
	private void precomputation()
	{
		for(Integer node : intraD.keySet())
		{
			for(Integer anotherNode : intraD.keySet())
			{
				if(node != anotherNode)
				{
					int intraDomainCost = dijkstra(node, anotherNode, 90);
					intraDomainLatencies.put(new PoPTuple(node, anotherNode), intraDomainCost);
				}
			}
		}
	}
	
	boolean stop = true;
	boolean precomputed = false;
	
	public int getIntraDomainCost(int pop1, int pop2, Integer advertisedToAS)
	{
		if (true)
			return 0;
		if(!precomputed)
		{
			precomputation();
			precomputed = true;
		}
		
		if(intraDomainLatencies.get(new PoPTuple(pop1, pop2)) == null)
		{
			return 0;
		}
		
		System.out.println("cache hit");
		return intraDomainLatencies.get(new PoPTuple(pop1, pop2));
		
	}
	
	private int dijkstra(int pop1, int pop2, Integer advertisedToAS)
	{
		//no intradomain topo information, return 0
		if(intraD.size() == 0)
			return 0;
		
		//priority queue of unvisted nodes
		PriorityQueue<Node> unsettledNodes = new PriorityQueue<Node>(intraD.size(), new Node());
		HashSet<Integer> settledNodes = new HashSet<Integer>();
		HashMap<Integer, Integer> distance = new HashMap<Integer, Integer>();
		
		//check to make sure that pop1 is in our domain
		if(!intraD.containsKey(pop1)){
		//	System.out.println("[DEBUG] no such intradomain connection");
			return 0;
		}
		//check to see if 0 hop from pop1 to pop2 (there is a direct connection from pop1 to pop2 in another AS, 0 cost)
		if(!intraD.containsKey(pop2) && neighborLatency.get(advertisedToAS).containsKey(new PoPTuple(pop1, pop2)))
		{
			System.out.println("0 intradomain hops to neighbor");
			return 0;
		}
		
		//otherwise there is no such connection, return 0 in that case i guess
		else if(!intraD.containsKey(pop2))
		{
	//		System.out.println("no such pop2");
			return 0;
		}
			
		for(Integer key : intraD.keySet())
		{
			if(key != pop1){
				unsettledNodes.add(new Node(key, Integer.MAX_VALUE));
				distance.put(key, Integer.MAX_VALUE);
			}
		}
		
		distance.put(pop1, 0);
		unsettledNodes.add(new Node(pop1, 0));
		
		while(!unsettledNodes.isEmpty())
		{
			Node evalNode = unsettledNodes.remove();			
			//evaluate neighbors
			HashMap<Integer, Integer> neighbors = intraD.get(evalNode.node);
			if(!settledNodes.contains(evalNode.node))
			{			
				settledNodes.add(evalNode.node);
			}			
			for(Integer key : neighbors.keySet())
			{
				if(!settledNodes.contains(key)){
					int potentialCost = neighbors.get(key) + evalNode.distance;//latency of adjacency + distance
					if(potentialCost < distance.get(key) )
					{
						distance.put(key, potentialCost);
						unsettledNodes.add(new Node(key, potentialCost));
					}
				}
			}
				
		}
		
		//if there was pop2 in our intradomain pops, but it wasn't updated, means that intradomain doesn't have path to
		//it (it is there, would have returned earlier if it wasn't
		if(distance.get(pop2) == Integer.MAX_VALUE){
		//	System.out.println("no path to pop2: " + pop2);
			return 0;
		}
	//	System.out.println("path to pop2: ");
		return distance.get(pop2);
		
	}
	
	

	//gets all the paths for a particular destination (in the ribIn)
	public abstract Collection<IA> getAllPaths(int dst);

	//resets the as
	public abstract void RESET();
	
	public String showNeighbors() {
		String nbrs = "Neighbors of BGP_AS" + asn + " Prov: " + providers + " Cust: " + customers + " Peer: " + peers;
		return nbrs;
	}
	
	public abstract String showFwdTable();
	
	/**
	 * 
	 * method that generates a list of updates for neighbor depending on PoP connections
	 * @param advert the created advertisement forneighbor
	 * @param neighbor the neighbor to send advertisemetn
	 * @return a list of advertisemetns for that neighbor
	 */
/*	ArrayList<IA> genPathforNeighbor(IA advert, int neighbor)
	{
		//grab pop links for this neighbor
		HashMap<PoPTuple, Integer> popConnections = neighborLatency.get(neighbor);
		ArrayList<IA> paths = new ArrayList<IA>();
		//for each pop link, craete a new path and update true cost (bookkeeping) depending on the latency between the poptuple
		//make the fromPoP equal to the Pop we are sending this out of
		for(PoPTuple popTuple : popConnections.keySet())
		{
			IA newAdvert = new IA(advert);
			long newCost = newAdvert.getTrueCost() + popConnections.get(popTuple);
			newAdvert.setTrueCost(newCost);
			newAdvert.setPoPTuple(popTuple);
			paths.add(newAdvert);
		}
		
		return paths;
		
	}*/



}
