package EastWest;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import protobuf.AdvertisementProtos.ProtoAdvertisement;
import protobuf.AdvertisementProtos.voidMessage;
import protobuf.EastWestGrpc;

public class RPCClient {
	private static final Logger logger = Logger
			.getLogger(RPCClient.class.getName());

	private  ChannelImpl channel;
	private  EastWestGrpc.EastWestBlockingStub blockingStub;

	private int port;
	private String host;



	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
	}

	public void sendAdvertisement(String host, int port,
			ProtoAdvertisement advert) {
		try {
			channel = NettyChannelBuilder.forAddress(host, port)
					.negotiationType(NegotiationType.PLAINTEXT).build();
			blockingStub = EastWestGrpc.newBlockingStub(channel);
			voidMessage reply = blockingStub.sendAdvertisement(advert);
			try {
				shutdown();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (RuntimeException e) {
			logger.log(Level.WARNING, "RPC failed", e);
			return;
		}
	}

}
