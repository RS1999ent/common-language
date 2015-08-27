package EastWest;

import CommonLanguageAdvertisement.CommonLanguageAdvertisement;
import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.logging.Logger;

import protobuf.AdvertisementProtos.ProtoAdvertisement;
import protobuf.AdvertisementProtos.voidMessage;
import protobuf.EastWestGrpc;

public class RPCServer implements Runnable {
	private static final Logger logger =  Logger.getLogger(RPCServer.class.getName());
	private static final long SLEEPTIME = 100;
	
	private int port = 50051;
	private String IP;
	private ServerImpl server;
	private Stack<CommonLanguageAdvertisement> receivedAdverts
		= new Stack<CommonLanguageAdvertisement>();
	private SendAdvertisementImpl rpcMethod;
	
	public RPCServer(int port, String IP)
	{
		this.port = port;
		this.IP = IP;
		
	}
	
	public RPCServer(){}
	
	private void start() throws Exception{
		this.rpcMethod = new SendAdvertisementImpl();
		
		server = NettyServerBuilder.forPort(port)
				.addService(EastWestGrpc.bindService(this.rpcMethod))
				.build().start();
		logger.info("Server started, listening on " + port);
		
		 Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        RPCServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
		
	}
	
	private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }
	
	public synchronized CommonLanguageAdvertisement popAdvertisement(){
		try {
			return receivedAdverts.pop();
		} catch (EmptyStackException e) {
			return null;
		}
	/*	if(advert != null){
			return advert;			
		}
		else 
		{
			return null;
		}*/
	}
	
	
	@Override
	public void run() {
		try {
			this.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (server != null)
		{
			CommonLanguageAdvertisement advert = rpcMethod.popAdvertisement();
			synchronized (receivedAdverts) {
				if (advert != null) {
					receivedAdverts.push(advert);
				}

				else {
					try {
						Thread.sleep(SLEEPTIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	private class SendAdvertisementImpl implements EastWestGrpc.EastWest{
		
		Stack<CommonLanguageAdvertisement> receivedAdverts = 
				new Stack<CommonLanguageAdvertisement>();
		
		
		
		@Override
		public void sendAdvertisement(ProtoAdvertisement adver, 
				StreamObserver<voidMessage> responseObserver){
			voidMessage reply = voidMessage.newBuilder().build();
			CommonLanguageAdvertisement deProtodAdvert = 
					new CommonLanguageAdvertisement(adver);
			
			synchronized (receivedAdverts) {
				receivedAdverts.add(deProtodAdvert);
				responseObserver.onValue(reply);
				responseObserver.onCompleted();
			}
		}
		
		//grabs an advertisement from the arraylist.  returns null if
		// there is none;
		public synchronized CommonLanguageAdvertisement popAdvertisement() {
			try {
				return receivedAdverts.pop();
			} catch (EmptyStackException e) {
				return null;
			}

		}
	
	}

	
	
}

