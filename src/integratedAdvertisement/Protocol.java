package integratedAdvertisement;

//import protobuf.AdvertisementProtos.ProtoClass;

/*class that holds the information associated with
identifying a protocol
*/
public class Protocol {

	private long m_uniqueID;  //globally unique id associated with class
	
	public Protocol(long uniqueID){
		m_uniqueID = uniqueID;
	}
	
	//takes in protobuf ProtoClass and extracts unique id from it
//	public Protocol(ProtoClass protoClass)
//	{
//		m_uniqueID = protoClass.getUniqueID();
//	}
	
	
	public long getUniqueID(){
		return m_uniqueID;
	}
	
	public void setUniqueID(long newUniqueID)
	{
		m_uniqueID = newUniqueID;
	}
	
	//converts data structure to protobuf format
//	public ProtoClass toProtoClass()
//	{
//		return ProtoClass.newBuilder().setUniqueID(m_uniqueID).build();
//	}
	
	public String toString()
	{
		return String.valueOf(m_uniqueID);
	}
	
}
