package CommonLanguageAdvertisement;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;


public class NetworkGraphTest {

	NetworkGraph filledGraph = new NetworkGraph();
	
	@Before
	public void setUp() throws Exception {
		filledGraph = new NetworkGraph();
		Random random = new Random();
		Class class1 = new Class(23);
		Class class2 = new Class(24);
		Class class3 = new Class(25);
		Node node1 = new Node(52, class1);
		Node node2 = new Node(53, class2);
		Node node3 = new Node(54, class3);
		
		filledGraph.addNode(node1);
		filledGraph.addConnection(node1, node2);
		filledGraph.addNode(node3);
		filledGraph.addConnection(node3, node1);
		
		filledGraph.addKeyValue("KEY1", class1, "test1".getBytes());
		filledGraph.addKeyValue("KEY1", class2, "test2".getBytes());
		
		filledGraph.addEdgeAnnotation(new EdgePair(node1, node2), 
				class1, "edgeAnnotation1".getBytes());
		
		System.out.println(filledGraph.toString());
	}

	@Test
	public void testNetworkGraphProtoNetworkGraph() {
		NetworkGraph newGraph = new NetworkGraph(
				filledGraph.toProtoNetworkGraph());
		System.out.println("");
		System.out.println(newGraph.toString());
		
	}

}
