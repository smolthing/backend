package com.backend.smolthing.db.redis;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import java.util.Objects;

public class RedisClientFactory {

  public static final String REDIS_PREFIX_USER = "user:%s";
  private static String redisUrl;
  private static RedisAPI redisApi;

  public static RedisAPI getClient() {
    if (Objects.isNull(redisApi)) {
      final Redis redisClient = Redis.createClient(Vertx.currentContext().owner(), redisUrl);
      redisApi = RedisAPI.api(redisClient);
    }
    return redisApi;
  }

  public static void setConfig(JsonObject config) {
    redisUrl = config.getString("url");
  }
}
