package com.backend.smolthing;

import com.backend.smolthing.db.MySQLVerticle;
import com.backend.smolthing.db.redis.RedisClientFactory;
import com.backend.smolthing.grpc.GrpcApiVerticle;
import com.backend.smolthing.http.HttpApiVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    ConfigRetriever retriever = ConfigRetriever.create(vertx,
      new ConfigRetrieverOptions().addStore(
        new ConfigStoreOptions()
          .setType("file")
          .setConfig(new JsonObject().put("path", "src/main/resources/config/config.json"))));

    retriever.getConfig().onComplete(json -> {
      JsonObject httpConfig = json.result().getJsonObject("http");
      JsonObject grpcConfig = json.result().getJsonObject("grpc");
      JsonObject mysqlConfig = json.result().getJsonObject("database");
      JsonObject redisConfig = json.result().getJsonObject("redis");

      RedisClientFactory.setConfig(redisConfig);

      vertx.deployVerticle(new HttpApiVerticle(), new DeploymentOptions().setConfig(httpConfig));
      vertx.deployVerticle(new GrpcApiVerticle(), new DeploymentOptions().setConfig(grpcConfig));
      vertx.deployVerticle(new MySQLVerticle(), new DeploymentOptions().setConfig(mysqlConfig));
    });
  }
}
