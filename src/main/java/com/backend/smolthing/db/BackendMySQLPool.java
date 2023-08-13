package com.backend.smolthing.db;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import javax.inject.Singleton;
import lombok.Getter;

@Singleton
public class BackendMySQLPool {
  @Getter
  private final Pool pool;

  public BackendMySQLPool() {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(3306)
      .setHost("localhost")
      .setDatabase("backend")
      .setUser("root")
      .setPassword("password")
      .setIdleTimeout(5);

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    pool = Pool.pool(Vertx.currentContext().owner(), connectOptions, poolOptions);
  }
}
