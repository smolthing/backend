package com.backend.smolthing.grpc;

import com.protobuf.schema.grpc.GetUserResponse;
import com.protobuf.schema.grpc.User;
import com.protobuf.schema.grpc.UserServiceGrpc;
import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.server.GrpcServer;
import java.io.IOException;

public class GrpcApiVerticle extends AbstractVerticle {
  @Override
  public void start() throws IOException {
    GrpcServer rpcServer = GrpcServer.server(vertx);
    rpcServer.callHandler(UserServiceGrpc.getGetUserMethod(), request -> {
      User user = User.newBuilder().setId(1).setName("smol").build();
      request.response().end(GetUserResponse.newBuilder().setUser(user).build());
    });

    vertx.createHttpServer().requestHandler(rpcServer).listen(9000, grpc -> {
      if (grpc.succeeded()) {
        System.out.println("gRPC server is running on port 9000");
      } else {
        System.err.println("Failed to start HTTP server: " + grpc.cause());
      }
    });
  }
}
