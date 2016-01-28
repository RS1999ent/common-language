/**
 * 
 */
package simulator;

import java.io.UnsupportedEncodingException;
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
import integratedAdvertisement.Protocol;
import integratedAdvertisement.RootCause;

/**
 * 
 * @author David
 *
 */
public abstract class AS {

	static final int DAMPEN_AFTER = 100;
	
	static final int PROVIDER = 1;
	static final int PEER = 0;
	static final int CUSTOMER = -1;
	static final int SIBLING = 2;
	
	static final int BGP = 500;
	static final int WISER = 501;
	static final int TRANSIT = 502;
	static final int SBGP_TRANSIT = 503;
	static final int SBGP = 504;
	static final int BANDWIDTH_AS = 505;
	static final int BANDWIDTH_TRANSIT = 506;
	static final int REPLACEMENT_AS = 507;
	static final int REPLACEMENT_TRANSIT = 506;
	
	static final int COST_METRIC = 1;
	static final int BW_METRIC = 2;
	
	
	public int type = 0;
	
	//did this as announce itself
	public boolean announced = false;
		/** Set of neighbors that are customers */
	ArrayList<Integer> customers = new ArrayList<Integer>();

	/** Set of neighbors that are providers */
	ArrayList<Integer> providers = new ArrayList<Integer>();

	/** Set of neighbors that are peers */
	ArrayList<Integer> peers = new ArrayList<Integer>();
	
	
	double resetPercent = 0; //percent chance to reset seencounter if past dampen thresh
	//holds seen counter for route dampening.  <destination, <path, count>>
	HashMap<Integer, HashMap<String, Integer>> seenCounter = new HashMap<Integer, HashMap<String, Integer>>();
	//destinations to stop counting
	ArrayList<Integer> doneDests = new ArrayList<Integer>(); 
	
	
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
		
		public PoPTuple reverse(){
			return new PoPTuple(pop2, pop1);
		}
		
	
	}

	// hashmap to find latencies. neighbor (int) -> hashmap of point of presense to hashmap of metric
	HashMap<Integer, HashMap<PoPTuple, HashMap<Integer, Float>>> neighborLatency = new HashMap<Integer, HashMap<PoPTuple, HashMap<Integer, Float>>>();

	// adjacency list for intradomain pop adjacencies. Goes Pop -> hash adjacentpop -> latency
	HashMap<Integer, HashMap<Integer, Integer>> intraD = new HashMap<Integer, HashMap<Integer, Integer>>();
	
	protected PassThrough passThrough = new PassThrough(); //enable passthroughfunctionality for AS
	
	/** Mapping of neighbor to relationship */
	HashMap<Integer, Integer> neighborMap = new HashMap<Integer, Integer>();

	// we also need to store all the paths received from neighbors for each
	// destination. this would be our rib-in. the rib-in is implemented as
	// a pair of nested hash tables: hashed on <prefix, neighbor>
	HashMap<Integer, HashMap<Integer,IA>> ribIn = new HashMap<Integer, HashMap<Integer, IA>>();

	
	public Integer asn;
	
	public Integer protocol;
	
	HashMap<Integer,IA> bestPath = new HashMap<Integer, IA>();

	public abstract boolean isBetter(IA p1, IA p2, boolean dampenBookKeeping);

	public abstract int getNextHop(int dst);

	public abstract void floodCompleted(Set<RootCause> allIncomplete);

	public abstract void handleEvent(Event e);

	public abstract void announceSelf();

	public abstract void addCustomer(int as1);

	public abstract void addPeer(int as2);

	public abstract void addProvider(int as2);
	
	/**
	 * adds the metric assigned to link between two pairs
	 * 
	 * @param as
	 *            the neighbor AS
	 * @param popPair
	 *            the points of presence connecting the two
	 * @param metricVal
	 *            the latency between those points of presence
	 */
	public void addLinkMetric(int as, PoPTuple popPair, int metric, float metricVal) {
		// grab reference to the has table if it exists for this neighbor
		HashMap<PoPTuple, HashMap<Integer, Float>> temp;
		if (!neighborLatency.containsKey(as)) {
			temp = new HashMap<PoPTuple, HashMap<Integer, Float>>();
			neighborLatency.put(as, temp);
		}
		temp = neighborLatency.get(as);
		if(!temp.containsKey(popPair))
		{
			temp.put(popPair, new HashMap<Integer, Float>());
		}
		// add the latency for the pair
		temp.get(popPair).put(metric, metricVal);

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

	private int getLowestCost(HashSet<Integer> nodes, HashMap<Integer, Integer> distance)
	{
		int lowestCost = Integer.MAX_VALUE;
		int lowKey = -1;
		for(Iterator<Integer> it = nodes.iterator(); it.hasNext();)
		{
			int node = it.next();
			if(distance.get(node).intValue() < lowestCost)
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
		for(int node : intraD.keySet())
		{
			for(int anotherNode : intraD.keySet())
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
/**
	 * method that returns wiser passthrough information in form of string
	 * @param advert advertisemet to wiser props from
	 * @return string of wiser props (split), null if none
	 */
	/**
	 * method that returns wiser passthrough information in form of string
	 * @param advert advertisement to get wiser props from
	 * @param forTuple, which point of presence tuple this information is associated with
	 * @return string of wiser props (split), null if none
	 */
	public static String[] getWiserProps(IA advert, PoPTuple forTuple)
	{
		
		byte[] pWiserBytes = advert.getProtocolPathAttribute(forTuple, new Protocol(AS.WISER), advert.getPath());
		if (pWiserBytes == null)
		{
			return null;
		}
		String pWiserProps = null;
		String[] splitProps = null;
		if(pWiserBytes[0] != (byte) 0xFF)
		{
			try {
				pWiserProps = new String(pWiserBytes, "UTF-8");
				return pWiserProps.split("\\s+");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return splitProps;
	}
	
	public static String[] getBandwidthProps(IA advert, PoPTuple forTuple)
	{
		byte[] pBandwidthBytes = advert.getProtocolPathAttribute(forTuple, new Protocol(AS.BANDWIDTH_AS), advert.getPath());
		String pBandwidthProps = null;
		String[] splitProps = null;
		if(pBandwidthBytes == null)
		{
			return null;
		}
		if(pBandwidthBytes[0] != (byte) 0xFF)
		{
			try{
				pBandwidthProps = new String(pBandwidthBytes, "UTF-8");
				return pBandwidthProps.split("\\s+");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return splitProps;
		
	}
	
	public static String[] getProtoProps(IA advert, PoPTuple forTuple, Protocol protocol)
	{
		byte[] protoBytes = advert.getProtocolPathAttribute(forTuple, protocol, advert.getPath());
		String protoProps = null;
		String[] splitProps = null;
		if(protoBytes == null)
		{
			return null;
		}
		if(protoBytes[0] != (byte) 0xFF)
		{
			try{
				protoProps = new String(protoBytes, "UTF-8");
				return protoProps.split("\\s+");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return splitProps;
	}

	
	/**
	 * method that clears the bookkeeping structures of an AS to conserve memory
	 * pendingupdates, dstribhist, ribin, 
	 */
	protected abstract void clearBookKeeping();


	/**
	 * updates bookKeeping information in an advertisement
	 * @param advert - advert to update
	 * @param chosenTuple - what tuple we are choicing from us to them
	 */
	protected  void updateBookKeeping(IA advert, PoPTuple chosenTuple){
		if(neighborLatency.containsKey(advert.getFirstHop()))
		{
			advert.setTrueCost(advert.getTrueCost() + neighborLatency.get(advert.getFirstHop()).get(chosenTuple).get(AS.COST_METRIC));
			if(!advert.bookKeepingInfo.containsKey(IA.BNBW_KEY))
			{				
				advert.bookKeepingInfo.put(IA.BNBW_KEY, Float.MAX_VALUE);
			}
			float currBNBW = Float.valueOf(advert.bookKeepingInfo.get(IA.BNBW_KEY));
			float neighborBW = neighborLatency.get(advert.getFirstHop()).get(chosenTuple).get(AS.BW_METRIC); 
			if( neighborBW < currBNBW)
			{
				advert.bookKeepingInfo.put(IA.BNBW_KEY, neighborBW );
			}
		}
		else
		{
			System.out.println("BW_as, can't update costs");
		}
	}	
	
	//DOES NOT WORK WITH MULTIPLUE POPS
	protected void updateBookKeepingOutward(IA advert, int toAS){
		if(neighborLatency.containsKey(toAS))
		{
			for(PoPTuple neighborTuple : neighborLatency.get(toAS).keySet())
			{
				advert.setTrueCost(advert.getTrueCost() + neighborLatency.get(toAS).get(neighborTuple).get(AS.COST_METRIC));
				if(!advert.bookKeepingInfo.containsKey(IA.BNBW_KEY))
				{				
					advert.bookKeepingInfo.put(IA.BNBW_KEY, Float.MAX_VALUE);
				}
				float currBNBW = Float.valueOf(advert.bookKeepingInfo.get(IA.BNBW_KEY));
				float neighborBW = neighborLatency.get(toAS).get(neighborTuple).get(AS.BW_METRIC); 
				if( neighborBW < currBNBW)
				{
					advert.bookKeepingInfo.put(IA.BNBW_KEY, neighborBW );
				}
			}
		}
		else
		{
			System.out.println("as, can't update costs");
		}
	}
	/**
	 * returns the puptuple of us to them that we choose as the point of presenes used on the path
	 * @param advert the advertisement that has the infomration to make this decision
	 * @return the tuple ponit of presence touble that we chose
	 */
	public abstract PoPTuple tupleChosen(IA advert);
	
	public boolean bestpathNullCheck()
	{
		for(IA element : bestPath.values())
		{
			if(element.getPath().isEmpty())
			{
				return true;
			}
		}
		return false;
	}
	
	protected int dampening(IA p1, IA p2)
	{
		//dampen bookkeeping
		int dest = p1.getDest();
		String p1PathKey = IA.pathToKey(p1.getPath());
		String p2PathKey = IA.pathToKey(p2.getPath());
		HashMap<String, Integer> seenPaths = null;
		if(!seenCounter.containsKey(dest))
		{
			HashMap<String, Integer> tmp = new HashMap<String, Integer>();
			seenCounter.put(dest, tmp);
		}
		seenPaths = seenCounter.get(dest);
		if(!seenPaths.containsKey(p1PathKey))
		{
			seenPaths.put(p1PathKey, 0);
		}
		if(!seenPaths.containsKey(p2PathKey))
		{
			seenPaths.put(p2PathKey, 0);
		}


		int p1Count = seenPaths.get(p1PathKey);
		int p2Count = seenPaths.get(p2PathKey);
	//	if(!doneDests.contains(p1.getDest())){
			seenPaths.put(p1PathKey, p1Count+1);
			seenPaths.put(p2PathKey, p2Count+1);
	//	}
		//else
		if(doneDests.contains(dest))
		{
			if(Simulator.r.nextDouble() < resetPercent)
			{
				seenPaths.clear();
				
				boolean test = doneDests.remove(new Integer(dest));
				if(!test)
				{
					System.out.println("dampening error");
				}
			}
			if(p1Count > p2Count/*Simulator.r.nextBoolean()*/)
			{
				return 1;
			}
			else{
				return 2;
			}
		}

		if(p1Count > AS.DAMPEN_AFTER || p2Count > AS.DAMPEN_AFTER)
		{
			if(!doneDests.contains(p1.getDest()))
			{
				doneDests.add(p1.getDest());
			}
		}

		return -1;
	}

}
