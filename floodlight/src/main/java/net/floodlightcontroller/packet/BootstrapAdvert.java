package net.floodlightcontroller.packet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BootstrapAdvert extends BasePacket {

	private String m_IP = "";
	public BootstrapAdvert(String ip)
	{
		m_IP = ip;
	}
	
	@Override
	public byte[] serialize() {
		byte[] data = m_IP.getBytes(Charset.forName("UTF-8"));		
		return data;		
	}

	@Override
	public IPacket deserialize(byte[] data, int offset, int length)
			throws PacketParsingException {
		ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
		
		m_IP = new String(bb.array(), Charset.forName("UTF-8"));
		return this;
	}

}
