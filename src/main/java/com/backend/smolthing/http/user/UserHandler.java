package com.backend.smolthing.http.user;

import com.backend.smolthing.db.dao.UserDaoImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import java.util.NoSuchElementException;
import javax.inject.Singleton;

@Singleton
public class UserHandler {

  private static final String USER_ID = "id";

  public static void handle(RoutingContext ctx) {
    final String userId = ctx.request().getParam(USER_ID);

    final UserDaoImpl userDaoImpl = new UserDaoImpl();
    userDaoImpl
      .getUser(Long.valueOf(userId))
      .compose(user -> {
        try {
          final String content = new ObjectMapper().writeValueAsString(user);
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
