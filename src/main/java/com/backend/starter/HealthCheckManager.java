package com.backend.starter;

import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;

public class HealthCheckManager {
  public static void configureHealthChecks(HealthCheckHandler healthCheckHandler) {
    healthCheckHandler.register("App connection", promise -> {
      promise.complete(Status.OK());
    });
  }
}
