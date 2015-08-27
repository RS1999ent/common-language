package CommonLanguageAdvertisement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Before;
import org.junit.Test; 

public class NodeTest {

	public static long asNum = 545;
	public Node fillNode = new Node(asNum, new ArrayList<Class>());
	ArrayList<Class> addedClasses = new ArrayList<Class>();
	
	@Before
	public void setUpBefore() throws Exception {
		fillNode = new Node(asNum, new ArrayList<Class>());
		Random random = new Random();		
		for(int i = 0; i < 50; i++)
		{
			Class newClass = new Class(random.nextLong());
			addedClasses.add(newClass);
			fillNode.addClass(newClass);			
		}
		ArrayList<Class> filledClasses = fillNode.getSupportedClasses();
		for(int i = 0; i < filledClasses.size(); i++)
		{
			//System.out.println(filledClasses.get(i).getUniqueID());
			assert(filledClasses.get(i).getUniqueID() == 
					addedClasses.get(i).getUniqueID());
		}
	}

	@Test
	public void testNodeProtoNode() {
		Node newNode = new Node(fillNode.toProtoNode());		
		assert (newNode.getASNum() == fillNode.getASNum());
		ArrayList<Class> fillNodeClasses = fillNode.getSupportedClasses();
		ArrayList<Class> newNodeClasses = newNode.getSupportedClasses();
		assert (fillNodeClasses.size() == newNodeClasses.size());		
		for(int i = 0; i < fillNodeClasses.size(); i++)
		{
			assert(fillNodeClasses.get(i).getUniqueID() 
					== newNodeClasses.get(i).getUniqueID());
		}
		
	}

//	@Test
//	public void testToProtoNode() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testFromProtoNode() {
//		fail("Not yet implemented");
//	}

}
