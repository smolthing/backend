package com.backend.smolthing.http.user;

import com.backend.smolthing.db.BackendMySQLPool;
import com.backend.smolthing.db.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import java.util.NoSuchElementException;

public class UserHandler {

  public static void handle(RoutingContext ctx) {
    String userId = ctx.request().getParam("id");

    BackendMySQLPool pool = new BackendMySQLPool(ctx.vertx());
    pool
      .getPool()
      .query(QueryStatement.SELECT_USER_BY_ID.formatted(userId))
      .execute()
      .map(rows -> rows.iterator().next())
      .map(UserEntity::fromDbRow)
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

  private static class QueryStatement {

    public static final String SELECT_USER_BY_ID
      = "SELECT `id`, `account_id`, `name`, `created_at`, `updated_at` "
      + "FROM `user` "
      + "WHERE id=%s LIMIT 1";
  }
}
