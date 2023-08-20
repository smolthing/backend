package com.backend.smolthing.db.dao;

import com.backend.smolthing.db.BackendMySQLPool;
import com.backend.smolthing.db.entity.UserEntity;
import io.vertx.core.Future;
import javax.inject.Singleton;

@Singleton
public class UserDaoImpl {

  static final private BackendMySQLPool pool = new BackendMySQLPool();

  public Future<UserEntity> getUser(long userId) {
    return pool
      .getPool()
      .query(QueryStatement.SELECT_USER_BY_ID.formatted(userId))
      .execute()
      .map(rows -> rows.iterator().next())
      .map(UserEntity::fromDbRow);
  }

  private static class QueryStatement {

    public static final String SELECT_USER_BY_ID
      = "SELECT `id`, `account_id`, `name`, `created_at`, `updated_at` "
      + "FROM `user` "
      + "WHERE id=%s LIMIT 1";
  }
}
