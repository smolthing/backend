package com.backend.smolthing.http.user;

import static com.backend.smolthing.db.redis.RedisClientFactory.REDIS_PREFIX_USER;

import com.backend.smolthing.db.dao.UserDaoImpl;
import com.backend.smolthing.db.entity.UserEntity;
import com.backend.smolthing.db.redis.RedisClientFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.RedisAPI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Logger;
import javax.inject.Singleton;

@Singleton
public class UserHandler {

  private static final Logger logger = Logger.getLogger("UserHandler.class");
  private static final RedisAPI redis = RedisClientFactory.getClient();
  private static final String USER_ID = "id";

  public static void handle(RoutingContext ctx) {
    final long userId = Long.parseLong(ctx.request().getParam(USER_ID));
    redis.get(REDIS_PREFIX_USER.formatted(userId)).onSuccess(cachedUser -> {
      if (Objects.nonNull(cachedUser)) {
        sendResponse(ctx, 200, cachedUser.toString());
      } else {
        getUserFromDatabase(userId, ctx);
      }
    }).onFailure(exception -> {
      logger.severe(exception.getMessage());
      getUserFromDatabase(userId, ctx);
    });
  }

  private static void getUserFromDatabase(long userId, RoutingContext ctx) {
    final UserDaoImpl userDaoImpl = new UserDaoImpl();
    userDaoImpl.getUser(userId)
      .onSuccess(userEntity -> cacheAndSendResponse(userEntity, userId, ctx))
      .onFailure(exception -> {
        if (exception instanceof NoSuchElementException) {
          sendResponse(ctx, 404);
          return;
        }
        logger.severe(exception.getMessage());

        sendResponse(ctx, 500);
      });
  }

  private static void cacheAndSendResponse(UserEntity userEntity, long userId, RoutingContext ctx) {
    try {
      String userInJSON = new ObjectMapper().writeValueAsString(userEntity);
      redis.set((List.of(REDIS_PREFIX_USER.formatted(userId), userInJSON)))
        .onSuccess(unused -> sendResponse(ctx, 200, userInJSON))
        .onFailure(exception -> logger.severe("Error setting user in redis: " + exception));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private static void sendResponse(RoutingContext ctx, int statusCode, String content) {
    ctx.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .setStatusCode(statusCode).setChunked(true).end(content);
  }

  private static void sendResponse(RoutingContext ctx, int statusCode) {
    ctx.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .setStatusCode(statusCode).setChunked(true).end();
  }
}
