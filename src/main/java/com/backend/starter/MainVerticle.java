package com.backend.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HealthCheckHandler healthCheckHandler = HealthCheckHandler
      .createWithHealthChecks(HealthChecks.create(vertx));
    HealthCheckManager.configureHealthChecks(healthCheckHandler);

    Router router = Router.router(vertx);
    router.get("/ping").handler(healthCheckHandler);
    router.get("/*").handler(routingContext -> {
      routingContext.response()
        .putHeader("content-type", "text/plain; charset=utf-8")
        .end("Hello smolthing \uD83D\uDCA9.");
    });

    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server is running on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
