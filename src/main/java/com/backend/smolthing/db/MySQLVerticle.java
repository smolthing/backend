package com.backend.smolthing.db;

import io.vertx.core.AbstractVerticle;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import java.util.Iterator;

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

      // All operations execute on the same connection
      return connection
        .query("SELECT * FROM user WHERE name='smol'")
        .execute()
        .onComplete(ar -> {
          connection.close();
        });
    }).onComplete(result -> {
      if (result.succeeded()) {
        RowSet<Row> rowSet = result.result();
        Iterator<Row> iterator = rowSet.iterator();
        if (iterator.hasNext()) {
          Row row = iterator.next();
          String name = row.getString("name");
          System.out.println("Found user: " + name);
          System.out.println(row.toJson());
        } else {
          System.out.println("No user found");
        }
      } else {
        System.out.println("Something went wrong " + result.cause().getMessage());
      }
    });
  }
}



