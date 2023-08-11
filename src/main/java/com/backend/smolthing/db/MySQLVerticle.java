package com.backend.smolthing.db;

import com.backend.smolthing.db.entity.UserEntity;
import io.vertx.core.AbstractVerticle;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class MySQLVerticle extends AbstractVerticle {
  @Override
  public void start() {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(3306)
      .setHost("localhost")
      .setDatabase("backend")
      .setUser("root")
      .setPassword("password")
      .setIdleTimeout(5);

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    Pool pool = Pool.pool(vertx, connectOptions, poolOptions);

    pool.getConnection().compose(connection -> {
      System.out.println("Got a connection from the pool");

      return connection
        .query("SELECT * FROM user WHERE name='smol'")
        .execute()
        .map(rows -> rows.iterator().next())
        .map(UserEntity::fromDbRow)
        .onComplete(ar -> {
          connection.close();
        });
    }).onComplete(result -> {
      if (result.succeeded()) {
        UserEntity user = result.result();
        System.out.println("Found user: " + user.getName());
        System.out.println("Account id: " + user.getAccountId());
        System.out.println("Created at: " + user.getCreatedAt());
        System.out.println("Updated at: " + user.getUpdatedAt());
      } else {
        System.out.println("Something went wrong " + result.cause().getMessage());
        result.cause().printStackTrace();
      }
    });
  }
}



