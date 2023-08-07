package com.backend.smolthing;

import com.backend.smolthing.grpc.GrpcApiVerticle;
import com.backend.smolthing.http.HttpApiVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle {
  @Override
  public void start() {
    vertx.deployVerticle(new HttpApiVerticle());
    vertx.deployVerticle(new GrpcApiVerticle());
  }
}
