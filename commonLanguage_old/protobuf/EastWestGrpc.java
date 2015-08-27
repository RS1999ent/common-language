package protobuf;

import static io.grpc.stub.Calls.createMethodDescriptor;
import static io.grpc.stub.Calls.asyncUnaryCall;
import static io.grpc.stub.Calls.asyncServerStreamingCall;
import static io.grpc.stub.Calls.asyncClientStreamingCall;
import static io.grpc.stub.Calls.duplexStreamingCall;
import static io.grpc.stub.Calls.blockingUnaryCall;
import static io.grpc.stub.Calls.blockingServerStreamingCall;
import static io.grpc.stub.Calls.unaryFutureCall;
import static io.grpc.stub.ServerCalls.createMethodDefinition;
import static io.grpc.stub.ServerCalls.asyncUnaryRequestCall;
import static io.grpc.stub.ServerCalls.asyncStreamingRequestCall;

@javax.annotation.Generated("by gRPC proto compiler")
public class EastWestGrpc {

  private static final io.grpc.stub.Method<protobuf.AdvertisementProtos.ProtoAdvertisement,
      protobuf.AdvertisementProtos.voidMessage> METHOD_SEND_ADVERTISEMENT =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "sendAdvertisement",
          io.grpc.protobuf.ProtoUtils.marshaller(protobuf.AdvertisementProtos.ProtoAdvertisement.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(protobuf.AdvertisementProtos.voidMessage.PARSER));

  public static EastWestStub newStub(io.grpc.Channel channel) {
    return new EastWestStub(channel, CONFIG);
  }

  public static EastWestBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new EastWestBlockingStub(channel, CONFIG);
  }

  public static EastWestFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new EastWestFutureStub(channel, CONFIG);
  }

  public static final EastWestServiceDescriptor CONFIG =
      new EastWestServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class EastWestServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<EastWestServiceDescriptor> {
    public final io.grpc.MethodDescriptor<protobuf.AdvertisementProtos.ProtoAdvertisement,
        protobuf.AdvertisementProtos.voidMessage> sendAdvertisement;

    private EastWestServiceDescriptor() {
      sendAdvertisement = createMethodDescriptor(
          "EastWest", METHOD_SEND_ADVERTISEMENT);
    }

    @SuppressWarnings("unchecked")
    private EastWestServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      sendAdvertisement = (io.grpc.MethodDescriptor<protobuf.AdvertisementProtos.ProtoAdvertisement,
          protobuf.AdvertisementProtos.voidMessage>) methodMap.get(
          CONFIG.sendAdvertisement.getName());
    }

    @java.lang.Override
    protected EastWestServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new EastWestServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          sendAdvertisement);
    }
  }

  public static interface EastWest {

    public void sendAdvertisement(protobuf.AdvertisementProtos.ProtoAdvertisement request,
        io.grpc.stub.StreamObserver<protobuf.AdvertisementProtos.voidMessage> responseObserver);
  }

  public static interface EastWestBlockingClient {

    public protobuf.AdvertisementProtos.voidMessage sendAdvertisement(protobuf.AdvertisementProtos.ProtoAdvertisement request);
  }

  public static interface EastWestFutureClient {

    public com.google.common.util.concurrent.ListenableFuture<protobuf.AdvertisementProtos.voidMessage> sendAdvertisement(
        protobuf.AdvertisementProtos.ProtoAdvertisement request);
  }

  public static class EastWestStub extends
      io.grpc.stub.AbstractStub<EastWestStub, EastWestServiceDescriptor>
      implements EastWest {
    private EastWestStub(io.grpc.Channel channel,
        EastWestServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected EastWestStub build(io.grpc.Channel channel,
        EastWestServiceDescriptor config) {
      return new EastWestStub(channel, config);
    }

    @java.lang.Override
    public void sendAdvertisement(protobuf.AdvertisementProtos.ProtoAdvertisement request,
        io.grpc.stub.StreamObserver<protobuf.AdvertisementProtos.voidMessage> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.sendAdvertisement), request, responseObserver);
    }
  }

  public static class EastWestBlockingStub extends
      io.grpc.stub.AbstractStub<EastWestBlockingStub, EastWestServiceDescriptor>
      implements EastWestBlockingClient {
    private EastWestBlockingStub(io.grpc.Channel channel,
        EastWestServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected EastWestBlockingStub build(io.grpc.Channel channel,
        EastWestServiceDescriptor config) {
      return new EastWestBlockingStub(channel, config);
    }

    @java.lang.Override
    public protobuf.AdvertisementProtos.voidMessage sendAdvertisement(protobuf.AdvertisementProtos.ProtoAdvertisement request) {
      return blockingUnaryCall(
          channel.newCall(config.sendAdvertisement), request);
    }
  }

  public static class EastWestFutureStub extends
      io.grpc.stub.AbstractStub<EastWestFutureStub, EastWestServiceDescriptor>
      implements EastWestFutureClient {
    private EastWestFutureStub(io.grpc.Channel channel,
        EastWestServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected EastWestFutureStub build(io.grpc.Channel channel,
        EastWestServiceDescriptor config) {
      return new EastWestFutureStub(channel, config);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<protobuf.AdvertisementProtos.voidMessage> sendAdvertisement(
        protobuf.AdvertisementProtos.ProtoAdvertisement request) {
      return unaryFutureCall(
          channel.newCall(config.sendAdvertisement), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final EastWest serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("EastWest")
      .addMethod(createMethodDefinition(
          METHOD_SEND_ADVERTISEMENT,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                protobuf.AdvertisementProtos.ProtoAdvertisement,
                protobuf.AdvertisementProtos.voidMessage>() {
              @java.lang.Override
              public void invoke(
                  protobuf.AdvertisementProtos.ProtoAdvertisement request,
                  io.grpc.stub.StreamObserver<protobuf.AdvertisementProtos.voidMessage> responseObserver) {
                serviceImpl.sendAdvertisement(request, responseObserver);
              }
            }))).build();
  }
}
