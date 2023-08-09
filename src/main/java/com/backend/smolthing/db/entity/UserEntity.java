package com.backend.smolthing.db.entity;

import io.vertx.sqlclient.Row;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UserEntity {

   long accountId;
   String name;
   LocalDateTime createdAt;
   LocalDateTime updatedAt;

  public static UserEntity fromDbRow(Row row) {
    return builder()
      .accountId(row.getLong("account_id"))
      .name(row.getString("name"))
      .createdAt(row.getLocalDateTime(("created_at")))
      .updatedAt(row.getLocalDateTime(("updated_at")))
      .build();
  }
}
