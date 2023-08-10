package com.backend.smolthing.http.user;

import com.backend.smolthing.http.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.ext.web.RoutingContext;

public class UserHandler {
  public static void handle(RoutingContext ctx) {
    String userId = ctx.request().getParam("id");
    String name = "smol";

    try {
      final var objectMapper = new ObjectMapper();
      UserDTO user = UserDTO.builder().id(Long.parseLong(userId)).name(name).build();
      final var content = objectMapper.writeValueAsString(user);
      ctx
        .put("responseBody", content)
        .response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200)
        .setChunked(true)
        .end(content);
    } catch (JsonProcessingException exception) {
      System.out.println(exception);
      ctx
        .response()
        .putHeader("content-type", "application/json")
        .setStatusCode(404)
        .end();
    }
  }
}
