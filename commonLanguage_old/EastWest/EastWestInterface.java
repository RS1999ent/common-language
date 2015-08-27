package EastWest;
import java.util.EmptyStackException;
import java.util.Stack;

import CommonLanguageAdvertisement.CommonLanguageAdvertisement;
public class EastWestInterface implements Runnable {

	private static final long SLEEPTIME = 1000;
	
	private RPCServer server = new RPCServer();
	private RPCClient client = new RPCClient();
	private Thread serverThread;
	
	private Stack<CommonLanguageAdvertisement> advertisements = 
			new Stack<CommonLanguageAdvertisement>();
	
	public EastWestInterface(int port, String IP)
	{
		server = new RPCServer(port, IP);
		serverThread = new Thread(server);
	}
	
	public synchronized CommonLanguageAdvertisement getNextAdvertisement()
	{
		try {
			return advertisements.pop();
		} catch (EmptyStackException e) {
			return null;
		}
//		if(advert != null)
//		{
//			return advert;
//		}
//		else{
//			return null;
//		}
	}
	
	public void sendAdvertisement(int port, String host, CommonLanguageAdvertisement advert)
	{
		client.sendAdvertisement(host, port, advert.toProtoAdvertisement());
	}
	
	@Override
	public void run() {
		serverThread.start();
		while(serverThread.isAlive())
		{
			synchronized (advertisements) {
				CommonLanguageAdvertisement advert = server.popAdvertisement();
				if (advert != null) {
					advertisements.push(advert);
				}
			}
			
			try {
				Thread.sleep(SLEEPTIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}

}
