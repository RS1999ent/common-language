package CommonLanguageAdvertisement;

import java.util.ArrayList;

import protobuf.AdvertisementProtos.ProtoClass;
import protobuf.AdvertisementProtos.ProtoNode;
import protobuf.AdvertisementProtos.ProtoNode.Builder;

//class that holds the information associated with a node
//on the graph for the common langauge advertisement.
public class Node {
	private long m_ASNum; //unique node identifier number. (currently 
						  //assumed to be asnumber.
	private ArrayList<Class> m_supportedClasses = 
			new ArrayList<Class>(); //list of classes the node supports
	
	public Node(long asNum, ArrayList<Class> supportedClases){
		m_ASNum = asNum;
		m_supportedClasses = supportedClases;
	}
	
	//takes protobuf node format and converts extracts relevent fields
	public Node(ProtoNode protoNode){
		m_ASNum = protoNode.getNodeNum();
		for(int i = 0; i < protoNode.getSupportedClassesCount(); i++)
		{
			m_supportedClasses.add(new Class(protoNode.getSupportedClasses(i)));
		}
	}
	
	//constructs a node with the common case that they ony have one class
	//more for convenience sake that you don't have to pass a list
	//every time
	public Node(long asNum, Class supportedClass) {
		m_ASNum = asNum;
		m_supportedClasses.add(supportedClass);
		
	}

	public long getASNum() {
		return m_ASNum;
	}

	public void setASNum(long m_ASNum) {
		this.m_ASNum = m_ASNum;
	}

	public ArrayList<Class> getSupportedClasses() {
		return m_supportedClasses;
	}

	public void setSupportedClasses(ArrayList<Class> m_supportedClasses) {
		this.m_supportedClasses = m_supportedClasses;
	}

	public void addClass(Class newClass)
	{
		if(!m_supportedClasses.contains(newClass))
		{
			m_supportedClasses.add(newClass);
		}
	}
	
	public void removeClass(Class removeClass)
	{
		if(!m_supportedClasses.contains(removeClass))
		{
			m_supportedClasses.remove(removeClass);
		}
	}
	
	//converts the node datastructure to the protobuf node type.
	public ProtoNode toProtoNode()
	{
		Builder protoNodeBuilder = ProtoNode.newBuilder();
//		int i = 0;
		for(Class supportedClass : m_supportedClasses)
		{
			protoNodeBuilder.addSupportedClasses(
					ProtoClass.newBuilder()
						.setUniqueID(supportedClass.getUniqueID())
						.build());
//			i++;
		}
		
		protoNodeBuilder.setNodeNum(m_ASNum);
		return protoNodeBuilder.build();
	}

	//used for an already existing node and extracting the protobuf
	//data.  See advertisement.proto
	public void fromProtoNode(ProtoNode protoNode) {
		m_ASNum = protoNode.getNodeNum();
		m_supportedClasses.clear();
		for (int i = 0; i < protoNode.getSupportedClassesCount(); i++) {
			m_supportedClasses.add(new Class(protoNode.getSupportedClasses(i)));
		}
	}
	
	//format < asnum, [list of suported classes] >
	public String toString()
	{
		String newString = "<";
		newString = newString.concat(String.valueOf(m_ASNum));
		newString = newString.concat(", ");
		newString = newString.concat(m_supportedClasses.toString());
		newString = newString.concat(">");
		return newString;
		
	}
	
}
