package com.gt.grpc;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: RingService.proto")
public final class RingServiceGrpc {

  private RingServiceGrpc() {}

  public static final String SERVICE_NAME = "com.gt.grpc.RingService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.gt.grpc.RingServiceOuterClass.Update,
      com.gt.grpc.RingServiceOuterClass.RingServiceResponse> METHOD_ENTER_NEXT =
      io.grpc.MethodDescriptor.<com.gt.grpc.RingServiceOuterClass.Update, com.gt.grpc.RingServiceOuterClass.RingServiceResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.gt.grpc.RingService", "enterNext"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.gt.grpc.RingServiceOuterClass.Update.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.gt.grpc.RingServiceOuterClass.RingServiceResponse.getDefaultInstance()))
          .setSchemaDescriptor(new RingServiceMethodDescriptorSupplier("enterNext"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.gt.grpc.RingServiceOuterClass.Update,
      com.gt.grpc.RingServiceOuterClass.RingServiceResponse> METHOD_ENTER_PREV =
      io.grpc.MethodDescriptor.<com.gt.grpc.RingServiceOuterClass.Update, com.gt.grpc.RingServiceOuterClass.RingServiceResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.gt.grpc.RingService", "enterPrev"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.gt.grpc.RingServiceOuterClass.Update.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.gt.grpc.RingServiceOuterClass.RingServiceResponse.getDefaultInstance()))
          .setSchemaDescriptor(new RingServiceMethodDescriptorSupplier("enterPrev"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.gt.grpc.RingServiceOuterClass.Update,
      com.gt.grpc.RingServiceOuterClass.RingServiceResponse> METHOD_QUIT_NEXT =
      io.grpc.MethodDescriptor.<com.gt.grpc.RingServiceOuterClass.Update, com.gt.grpc.RingServiceOuterClass.RingServiceResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.gt.grpc.RingService", "quitNext"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.gt.grpc.RingServiceOuterClass.Update.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.gt.grpc.RingServiceOuterClass.RingServiceResponse.getDefaultInstance()))
          .setSchemaDescriptor(new RingServiceMethodDescriptorSupplier("quitNext"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.gt.grpc.RingServiceOuterClass.Update,
      com.gt.grpc.RingServiceOuterClass.RingServiceResponse> METHOD_QUIT_PREV =
      io.grpc.MethodDescriptor.<com.gt.grpc.RingServiceOuterClass.Update, com.gt.grpc.RingServiceOuterClass.RingServiceResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.gt.grpc.RingService", "quitPrev"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.gt.grpc.RingServiceOuterClass.Update.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.gt.grpc.RingServiceOuterClass.RingServiceResponse.getDefaultInstance()))
          .setSchemaDescriptor(new RingServiceMethodDescriptorSupplier("quitPrev"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RingServiceStub newStub(io.grpc.Channel channel) {
    return new RingServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new RingServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new RingServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class RingServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void enterNext(com.gt.grpc.RingServiceOuterClass.Update request,
        io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ENTER_NEXT, responseObserver);
    }

    /**
     */
    public void enterPrev(com.gt.grpc.RingServiceOuterClass.Update request,
        io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ENTER_PREV, responseObserver);
    }

    /**
     */
    public void quitNext(com.gt.grpc.RingServiceOuterClass.Update request,
        io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_QUIT_NEXT, responseObserver);
    }

    /**
     */
    public void quitPrev(com.gt.grpc.RingServiceOuterClass.Update request,
        io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_QUIT_PREV, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_ENTER_NEXT,
            asyncUnaryCall(
              new MethodHandlers<
                com.gt.grpc.RingServiceOuterClass.Update,
                com.gt.grpc.RingServiceOuterClass.RingServiceResponse>(
                  this, METHODID_ENTER_NEXT)))
          .addMethod(
            METHOD_ENTER_PREV,
            asyncUnaryCall(
              new MethodHandlers<
                com.gt.grpc.RingServiceOuterClass.Update,
                com.gt.grpc.RingServiceOuterClass.RingServiceResponse>(
                  this, METHODID_ENTER_PREV)))
          .addMethod(
            METHOD_QUIT_NEXT,
            asyncUnaryCall(
              new MethodHandlers<
                com.gt.grpc.RingServiceOuterClass.Update,
                com.gt.grpc.RingServiceOuterClass.RingServiceResponse>(
                  this, METHODID_QUIT_NEXT)))
          .addMethod(
            METHOD_QUIT_PREV,
            asyncUnaryCall(
              new MethodHandlers<
                com.gt.grpc.RingServiceOuterClass.Update,
                com.gt.grpc.RingServiceOuterClass.RingServiceResponse>(
                  this, METHODID_QUIT_PREV)))
          .build();
    }
  }

  /**
   */
  public static final class RingServiceStub extends io.grpc.stub.AbstractStub<RingServiceStub> {
    private RingServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RingServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RingServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RingServiceStub(channel, callOptions);
    }

    /**
     */
    public void enterNext(com.gt.grpc.RingServiceOuterClass.Update request,
        io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ENTER_NEXT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void enterPrev(com.gt.grpc.RingServiceOuterClass.Update request,
        io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ENTER_PREV, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void quitNext(com.gt.grpc.RingServiceOuterClass.Update request,
        io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_QUIT_NEXT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void quitPrev(com.gt.grpc.RingServiceOuterClass.Update request,
        io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_QUIT_PREV, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class RingServiceBlockingStub extends io.grpc.stub.AbstractStub<RingServiceBlockingStub> {
    private RingServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RingServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RingServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RingServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.gt.grpc.RingServiceOuterClass.RingServiceResponse enterNext(com.gt.grpc.RingServiceOuterClass.Update request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ENTER_NEXT, getCallOptions(), request);
    }

    /**
     */
    public com.gt.grpc.RingServiceOuterClass.RingServiceResponse enterPrev(com.gt.grpc.RingServiceOuterClass.Update request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ENTER_PREV, getCallOptions(), request);
    }

    /**
     */
    public com.gt.grpc.RingServiceOuterClass.RingServiceResponse quitNext(com.gt.grpc.RingServiceOuterClass.Update request) {
      return blockingUnaryCall(
          getChannel(), METHOD_QUIT_NEXT, getCallOptions(), request);
    }

    /**
     */
    public com.gt.grpc.RingServiceOuterClass.RingServiceResponse quitPrev(com.gt.grpc.RingServiceOuterClass.Update request) {
      return blockingUnaryCall(
          getChannel(), METHOD_QUIT_PREV, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RingServiceFutureStub extends io.grpc.stub.AbstractStub<RingServiceFutureStub> {
    private RingServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RingServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RingServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RingServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> enterNext(
        com.gt.grpc.RingServiceOuterClass.Update request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ENTER_NEXT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> enterPrev(
        com.gt.grpc.RingServiceOuterClass.Update request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ENTER_PREV, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> quitNext(
        com.gt.grpc.RingServiceOuterClass.Update request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_QUIT_NEXT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.gt.grpc.RingServiceOuterClass.RingServiceResponse> quitPrev(
        com.gt.grpc.RingServiceOuterClass.Update request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_QUIT_PREV, getCallOptions()), request);
    }
  }

  private static final int METHODID_ENTER_NEXT = 0;
  private static final int METHODID_ENTER_PREV = 1;
  private static final int METHODID_QUIT_NEXT = 2;
  private static final int METHODID_QUIT_PREV = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RingServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RingServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ENTER_NEXT:
          serviceImpl.enterNext((com.gt.grpc.RingServiceOuterClass.Update) request,
              (io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse>) responseObserver);
          break;
        case METHODID_ENTER_PREV:
          serviceImpl.enterPrev((com.gt.grpc.RingServiceOuterClass.Update) request,
              (io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse>) responseObserver);
          break;
        case METHODID_QUIT_NEXT:
          serviceImpl.quitNext((com.gt.grpc.RingServiceOuterClass.Update) request,
              (io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse>) responseObserver);
          break;
        case METHODID_QUIT_PREV:
          serviceImpl.quitPrev((com.gt.grpc.RingServiceOuterClass.Update) request,
              (io.grpc.stub.StreamObserver<com.gt.grpc.RingServiceOuterClass.RingServiceResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RingServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.gt.grpc.RingServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RingService");
    }
  }

  private static final class RingServiceFileDescriptorSupplier
      extends RingServiceBaseDescriptorSupplier {
    RingServiceFileDescriptorSupplier() {}
  }

  private static final class RingServiceMethodDescriptorSupplier
      extends RingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RingServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RingServiceFileDescriptorSupplier())
              .addMethod(METHOD_ENTER_NEXT)
              .addMethod(METHOD_ENTER_PREV)
              .addMethod(METHOD_QUIT_NEXT)
              .addMethod(METHOD_QUIT_PREV)
              .build();
        }
      }
    }
    return result;
  }
}
