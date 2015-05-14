package CommonLanguageAdvertisement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import protobuf.AdvertisementProtos.ProtoAdjacentNodes;
import protobuf.AdvertisementProtos.ProtoNetworkGraph;
import protobuf.AdvertisementProtos.ProtoNetworkGraph.Builder;
import protobuf.AdvertisementProtos.ProtoNetworkGraph.annotatedEdgesFieldEntry;
import protobuf.AdvertisementProtos.ProtoNetworkGraph.keyValuePairsFieldEntry;

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
	
	
	private Hashtable<Node, ArrayList<Node>> adjacencyList = new Hashtable<Node, ArrayList<Node>>();
	private Hashtable<String, Values> keyValues = new Hashtable<String, Values>();
	private Hashtable<EdgePair, Values> edgeAnnotation = new Hashtable<EdgePair, Values>();
	public NetworkGraph(){
		
	}
	
	public NetworkGraph(ProtoNetworkGraph protoNetworkGraph)
	{
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
		
		for(int i = 0; i < protoNetworkGraph.getKeyValuePairsCount(); i++)
		{
			keyValuePairsFieldEntry entry = protoNetworkGraph.getKeyValuePairs(i);
			keyValues.put(entry.getKey(), new Values(entry.getValues()));
		}
		
		for(int i = 0; i < protoNetworkGraph.getAnnotatedEdgesCount(); i++)
		{
			annotatedEdgesFieldEntry entry = protoNetworkGraph.getAnnotatedEdges(i);
			EdgePair pair = new EdgePair(entry.getEdgePair());
			edgeAnnotation.put(pair, new Values(entry.getAnnotations()));
		}
		
	}
	
	
	
	
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


	public void addNode(Node add){	
		if(!adjacencyList.containsKey(add))
		{
			adjacencyList.put(add, new ArrayList<Node>());
		}
	}
	
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
