package com.backend.smolthing.http;

import com.backend.smolthing.http.user.UserHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;

public class HttpApiVerticle extends AbstractVerticle {

  @Override
  public void start() {
    HealthCheckHandler healthCheckHandler = HealthCheckHandler
      .createWithHealthChecks(HealthChecks.create(vertx));
    HealthCheckManager.configureHealthChecks(healthCheckHandler);

    Router router = Router.router(vertx);
    router.get("/ping").handler(healthCheckHandler);
    router.get("/users/:id").handler(UserHandler::handle);
    router.get("/*").handler(routingContext -> {
      routingContext.response()
        .putHeader("content-type", "text/plain; charset=utf-8")
        .end("Hello smolthing \uD83D\uDCA9, it's 404");
    });

    vertx.createHttpServer().requestHandler(router).listen(8000, http -> {
      if (http.succeeded()) {
        System.out.println("HTTP server is running on port 8000");
      } else {
        System.err.println("Failed to start HTTP server: " + http.cause());
      }
    });
  }
}
