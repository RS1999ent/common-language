package CommonLanguageAdvertisement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import protobuf.AdvertisementProtos.ProtoAdjacentNodes;
import protobuf.AdvertisementProtos.ProtoNetworkGraph;
import protobuf.AdvertisementProtos.ProtoNetworkGraph.Builder;
import protobuf.AdvertisementProtos.ProtoNetworkGraph.annotatedEdgesFieldEntry;
import protobuf.AdvertisementProtos.ProtoNetworkGraph.keyValuePairsFieldEntry;

//class that holds the network graph with all the annotations and
//key value pairs for classes.

public class NetworkGraph {
	/*import java.util.Hashtable;
	import java.util.Set;
	 
	public class MyHashtableRead {
	 
	    public static void main(String a[]){
	         
	        Hashtable<String, String> hm = new Hashtable<String, String>();
	        //add key-value pair to Hashtable
	        hm.put("first", "FIRST INSERTED");
	        hm.put("second", "SECOND INSERTED");
	        hm.put("third","THIRD INSERTED");
	        System.out.println(hm);
	        Set<String> keys = hm.keySet();
	        for(String key: keys){
	            System.out.println("Value of "+key+" is: "+hm.get(key));
	        }
	    }
	}
	*/
	
	
	private Hashtable<Node, ArrayList<Node>> adjacencyList =
			new Hashtable<Node, ArrayList<Node>>();//holds the structure
												   //of the graph in
	                                               //an adjacency list
	private Hashtable<String, Values> keyValues =
			new Hashtable<String, Values>();// holds key value pairs.
	 								//uses the values class so that multiple
									//protocols can key in on the same key
								   //and see what others have put in that key
	 							  //as well.
	private Hashtable<EdgePair, Values> edgeAnnotation = 
			new Hashtable<EdgePair, Values>(); //allows protocols to
	                                           //put annotations on
											   //edges if they want.
					
	public NetworkGraph(){
		
	}
	
	//takes in the protobuf format and extracts the relevent fields into
	//the adjacency list and hash tables.
	public NetworkGraph(ProtoNetworkGraph protoNetworkGraph)
	{
		//fill adjacency list
		for(int i = 0; i < protoNetworkGraph.getAdjacencyListCount(); i++)
		{
			ProtoAdjacentNodes headAndNeighbor = protoNetworkGraph.getAdjacencyList(i);
			ArrayList<Node> neighborList = new ArrayList<Node>();
			for(int j = 0; j < headAndNeighbor.getNeighborsCount(); j++)
			{
				neighborList.add(new Node(headAndNeighbor.getNeighbors(j)));
			}
			adjacencyList.put(new Node(headAndNeighbor.getReferenceNode()), neighborList );
		}
		
		//fill key value pairs
		for(int i = 0; i < protoNetworkGraph.getKeyValuePairsCount(); i++)
		{
			keyValuePairsFieldEntry entry = protoNetworkGraph.getKeyValuePairs(i);
			keyValues.put(entry.getKey(), new Values(entry.getValues()));
		}
		
		//fill annotated edges
		for(int i = 0; i < protoNetworkGraph.getAnnotatedEdgesCount(); i++)
		{
			annotatedEdgesFieldEntry entry = protoNetworkGraph.getAnnotatedEdges(i);
			EdgePair pair = new EdgePair(entry.getEdgePair());
			edgeAnnotation.put(pair, new Values(entry.getAnnotations()));
		}
		
	}
	
	
	//method that connects a node in the adjency list to some other node
	//toConnect - the node you want to connect to another node
	//connectTo - the node you want to connect to.  
	//Directed edge from toConnect to connectTo.  toConnect must already be
	//a leading node in the adjacency list.
	public void addConnection(Node toConnect, Node connectTo)
	{
		if(!adjacencyList.containsKey(toConnect))
		{
			ArrayList<Node> toPut = new ArrayList<Node>();
			toPut.add(connectTo);
			adjacencyList.put(connectTo, toPut);
		}
		else{
			ArrayList<Node> addConnection = adjacencyList.get(toConnect);
			if(!addConnection.contains(connectTo))
			{
				addConnection.add(connectTo);
			}
		}
		
		EdgePair edgePair = new EdgePair();
		edgePair.node1 = toConnect;
		edgePair.node2 = connectTo;
		if(!edgeAnnotation.containsKey(edgePair))
		{
			edgeAnnotation.put(edgePair, new Values());
		}
	}

	//adds a head node to the adjacency list
	public void addNode(Node add){	
		if(!adjacencyList.containsKey(add))
		{
			adjacencyList.put(add, new ArrayList<Node>());
		}
	}
	
	//adds values to the key value pairs with the value being associateed
	//with a particualr class.
	public void addKeyValue(String key, Class associatedClass,  byte[] value){
		Values values = keyValues.get(key);
		if(values != null)
		{
			values.putValue(associatedClass, value);
		}
		else
		{
			values = new Values();
			values.putValue(associatedClass, value);
			keyValues.put(key, values);
		}
	}
	
	//adds an edge annotation.  convention is the edge is directed.
	public void addEdgeAnnotation(EdgePair edge, Class associatedClass, byte[] value)
	{
		Values values = edgeAnnotation.get(edge);
		if(values != null)
		{
			values.putValue(associatedClass, value);
		}
		else
		{
			values = new Values();
			values.putValue(associatedClass, value);
			edgeAnnotation.put(edge, values);
		}
	}
	
	//converts this datastructure into protobuf format.
	public ProtoNetworkGraph toProtoNetworkGraph()
	{
		Builder protoNetworkGraphBuilder = ProtoNetworkGraph.newBuilder();
		makeProtoAdjacencyList(protoNetworkGraphBuilder);
		makeProtoAnnotatedEdges(protoNetworkGraphBuilder);
		makeProtoKeyValuePairs(protoNetworkGraphBuilder);
		
		return protoNetworkGraphBuilder.build();
	}
	
	public String toString()
	{
		String newString = adjacencyList.toString();
		newString = newString.concat("\n");
		newString = newString.concat(keyValues.toString());
		newString = newString.concat("\n");
		newString = newString.concat(edgeAnnotation.toString());
		return newString;
	}
	
	
	private void makeProtoAdjacencyList(Builder protoNetworkGraphBuilder)
	{
		Set<Node> keys = adjacencyList.keySet();
		int i = 0;	
		for(Node key: keys)
		{
			ArrayList<Node> neighborList = adjacencyList.get(key);
			ProtoAdjacentNodes.Builder adjacentNodesBuilder = ProtoAdjacentNodes.newBuilder();
			adjacentNodesBuilder.setReferenceNode(key.toProtoNode());
			
			for(int j  = 0; j < neighborList.size(); j++)
			{
				adjacentNodesBuilder.addNeighbors(neighborList.get(j).toProtoNode());
			}
			
			protoNetworkGraphBuilder.addAdjacencyList(adjacentNodesBuilder.build());
			i++;
		}
	}
	
	private void makeProtoAnnotatedEdges(Builder protoNetworkGraphBuilder)
	{
		Set<EdgePair> keys = edgeAnnotation.keySet();
		int i = 0;
		for(EdgePair key : keys)
		{
			protobuf.AdvertisementProtos.ProtoNetworkGraph.annotatedEdgesFieldEntry.Builder edgeEntryBuilder = annotatedEdgesFieldEntry.newBuilder();
			edgeEntryBuilder.setEdgePair(key.toProtoEdgeNodes());
			edgeEntryBuilder.setAnnotations(edgeAnnotation.get(key).toProtoValues());
			protoNetworkGraphBuilder.addAnnotatedEdges(edgeEntryBuilder.build());
			i++;
		}
	}
	
	private void makeProtoKeyValuePairs(Builder protoNetworkGraphBuilder)
	{
		Set<String> keys = keyValues.keySet();
		int i = 0;
		for(String key: keys)
		{
			protobuf.AdvertisementProtos.ProtoNetworkGraph.keyValuePairsFieldEntry.Builder keyValuePairsBuilder = keyValuePairsFieldEntry.newBuilder()
					.setKey(key)
					.setValues(keyValues.get(key).toProtoValues());
			protoNetworkGraphBuilder.addKeyValuePairs(keyValuePairsBuilder.build());		
			i++;
		}
	}
	
	
	
	
	
	
	
	
}
