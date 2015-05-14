/**
 * 
 */

/**
 * @author David
 *
 */

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import protobuf.AdvertisementProtos.ProtoAdvertisement;
import CommonLanguageAdvertisement.CommonLanguageAdvertisement;

public class FloodLightInterface implements Runnable {

	private ArrayList<CommonLanguageAdvertisement> advertisementList;
	private Socket floodLightConnection;
	private int floodLightPort;
	private String floodLightIP;
	//private BufferedReader in;
	private InputStream in;
	private BufferedOutputStream out;

	public FloodLightInterface(String floodLightIP, int floodLightPort) {
		floodLightConnection = new Socket();
		this.floodLightIP = floodLightIP;
		this.floodLightPort = floodLightPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			floodLightConnection.connect(new InetSocketAddress(InetAddress
					.getByName(floodLightIP), floodLightPort));
			//in = new BufferedReader(new InputStreamReader(
			//		floodLightConnection.getInputStream()));
			in = floodLightConnection.getInputStream();
			out = new BufferedOutputStream(
					floodLightConnection.getOutputStream());
			while(floodLightConnection.isConnected())
			{
				ProtoAdvertisement advertisement = ProtoAdvertisement.parseDelimitedFrom(in);
				synchronized(this){
					advertisementList.add(new CommonLanguageAdvertisement(advertisement));
				}
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
