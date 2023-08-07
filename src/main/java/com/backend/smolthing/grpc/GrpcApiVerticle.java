package com.backend.smolthing.grpc;

import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.server.GrpcServer;
import java.io.IOException;

public class GrpcApiVerticle extends AbstractVerticle {
  @Override
  public void start() throws IOException {
    GrpcServer grpcServer = GrpcServer.server(vertx);

   GetUserHandler.handle(grpcServer);

    vertx.createHttpServer().requestHandler(grpcServer).listen(9000, grpc -> {
      if (grpc.succeeded()) {
        System.out.println("gRPC server is running on port 9000");
      } else {
        System.err.println("Failed to start HTTP server: " + grpc.cause());
      }
    });
  }
}
