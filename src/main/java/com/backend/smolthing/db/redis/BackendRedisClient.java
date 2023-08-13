package com.backend.smolthing.db.redis;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import lombok.Getter;

public class BackendRedisClient {
  @Getter
  private final RedisAPI redisApi;

  public static final String REDIS_PREFIX_USER = "user:%s";

  public BackendRedisClient() {
    final Redis redisClient = Redis.createClient(
      Vertx.currentContext().owner(),
      "redis://:password@localhost:6379");

     redisApi = RedisAPI.api(redisClient);
  }
}
