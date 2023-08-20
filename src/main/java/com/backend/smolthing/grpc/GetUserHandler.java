package com.backend.smolthing.grpc;

import com.backend.smolthing.db.redis.RedisClientFactory;
import com.protobuf.schema.grpc.GetUserResponse;
import com.protobuf.schema.grpc.User;
import com.protobuf.schema.grpc.UserServiceGrpc;
import io.vertx.grpc.common.GrpcStatus;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.redis.client.RedisAPI;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

@Singleton
public class GetUserHandler {

  private static final Map<Long, String> listOfUsers = new HashMap<>();

  static {
    listOfUsers.put(1L, "smol");
    listOfUsers.put(2L, "small");
    listOfUsers.put(3L, "tiny");
  }

  public static void handle(GrpcServer grpcServer) {
    grpcServer.callHandler(UserServiceGrpc.getGetUserMethod(), request -> {
      request.handler(req -> {
        final long userId = req.getId();
        final boolean hasUsername = listOfUsers.containsKey(userId);

        if (hasUsername) {
          User user = User.newBuilder().setId(userId).setName(listOfUsers.get(userId)).build();
          request.response().end(GetUserResponse.newBuilder().setUser(user).build());
        } else {
          request.response().status(GrpcStatus.NOT_FOUND).end();
        }
      });
    });
  }
}
