package net.floodlightcontroller.packet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BootstrapAdvert extends BasePacket {

	private String m_IP = "";
	
	public BootstrapAdvert(){}
	
	public BootstrapAdvert(String ip)
	{
		m_IP = ip;
	}
	
	public String getNeighborIP()
	{
		return m_IP;
	}
	
	@Override
	public byte[] serialize() {
		byte[] data = m_IP.getBytes(Charset.forName("UTF-8"));
		//System.out.println("SERIALIZING BOOTSTRAP");
		return data;		
	}

	@Override
	public IPacket deserialize(byte[] data, int offset, int length)
			throws PacketParsingException {
		ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
		byte[] arr = new byte[length];
		bb.get(arr);
		m_IP = new String(arr, Charset.forName("UTF-8"));
		//System.out.println("DESERIALIZING BOOTSTRAP");
		return this;
	}

}
