package com.backend.smolthing.http;

import com.backend.smolthing.http.user.UserHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;

public class HttpApiVerticle extends AbstractVerticle {

  @Override
  public void start() {
    final HealthCheckHandler healthCheckHandler = HealthCheckHandler.createWithHealthChecks(
      HealthChecks.create(vertx));
    HealthCheckManager.configureHealthChecks(healthCheckHandler);

    final Router router = Router.router(vertx);
    router.get("/ping").handler(healthCheckHandler);
    router.get("/users/:id").handler(UserHandler::handle);
    router.get("/*").handler(routingContext -> routingContext.response()
      .putHeader("content-type", "text/plain; charset=utf-8")
      .end("Hello smolthing \uD83D\uDCA9, it's 404"));

    final int port = config().getInteger("port");
    vertx.createHttpServer().requestHandler(router).listen(port, http -> {
      if (http.succeeded()) {
        System.out.printf("HTTP server is running on port %d%n", port);
      } else {
        System.err.println("Failed to start HTTP server: " + http.cause());
      }
    });
  }
}
