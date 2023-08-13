package com.backend.smolthing.http.user;

import static com.backend.smolthing.db.redis.BackendRedisClient.REDIS_PREFIX_USER;

import com.backend.smolthing.db.dao.UserDaoImpl;
import com.backend.smolthing.db.redis.BackendRedisClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.RedisAPI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.inject.Singleton;

@Singleton
public class UserHandler {

  static final RedisAPI redis = new BackendRedisClient().getRedisApi();
  private static final String USER_ID = "id";

  public static void handle(RoutingContext ctx) {
    final String userId = ctx.request().getParam(USER_ID);

    redis.get(REDIS_PREFIX_USER.formatted(userId))
      .onSuccess(cachedUser -> {
        if (Objects.nonNull(cachedUser)) {
          ctx
            .response()
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .setStatusCode(200)
            .setChunked(true)
            .end(cachedUser.toString());
        } else {
          getUserFromDatabase(Long.valueOf(userId), ctx);
        }
      });
  }

  private static void getUserFromDatabase(long userId, RoutingContext ctx) {
    final UserDaoImpl userDaoImpl = new UserDaoImpl();
    userDaoImpl
      .getUser(userId)
      .compose(userEntity -> {
        try {
          String jsonString = new ObjectMapper().writeValueAsString(userEntity);
          return redis.set((List.of(REDIS_PREFIX_USER.formatted(userId), jsonString)))
            .compose(unused -> {
              try {
                final String content = new ObjectMapper().writeValueAsString(userEntity);
                return ctx
                  .response()
                  .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                  .setStatusCode(200)
                  .setChunked(true)
                  .end(content);
              } catch (JsonProcessingException exception) {
                return Future.failedFuture(exception.getMessage());
              }
            })
            .onFailure(exception -> {
              System.err.println("Error setting user data in redis cache");
            });
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      })
      .onFailure(exception -> {
        System.err.println(exception);
        if (exception instanceof NoSuchElementException) {
          ctx
            .response()
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .setStatusCode(404)
            .end();
          return;
        }
        ctx
          .response()
          .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(500)
          .end();
      });
  }
}
