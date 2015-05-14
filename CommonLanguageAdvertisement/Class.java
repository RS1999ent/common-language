package CommonLanguageAdvertisement;

import protobuf.AdvertisementProtos.ProtoClass;

public class Class {

	private long m_uniqueID;
	
	public Class(long uniqueID){
		m_uniqueID = uniqueID;
	}
	
	public Class(ProtoClass protoClass)
	{
		m_uniqueID = protoClass.getUniqueID();
	}
	
	
	public long getUniqueID(){
		return m_uniqueID;
	}
	
	public void setUniqueID(long newUniqueID)
	{
		m_uniqueID = newUniqueID;
	}
	
	public ProtoClass toProtoClass()
	{
		return ProtoClass.newBuilder().setUniqueID(m_uniqueID).build();
	}
	
	public String toString()
	{
		return String.valueOf(m_uniqueID);
	}
	
}
