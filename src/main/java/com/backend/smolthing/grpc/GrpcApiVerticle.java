package com.backend.smolthing.grpc;

import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.server.GrpcServer;
import java.io.IOException;

public class GrpcApiVerticle extends AbstractVerticle {

  @Override
  public void start() throws IOException {
    final GrpcServer grpcServer = GrpcServer.server(vertx);
    GetUserHandler.handle(grpcServer);

    final int port = config().getInteger("port");
    vertx.createHttpServer().requestHandler(grpcServer).listen(port, grpc -> {
      if (grpc.succeeded()) {
        System.out.println("gRPC server is running on port %d".formatted(port));
      } else {
        System.err.println("Failed to start HTTP server: " + grpc.cause());
      }
    });
  }
}
