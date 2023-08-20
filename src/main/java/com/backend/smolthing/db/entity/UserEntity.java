package com.backend.smolthing.db.entity;

import io.vertx.sqlclient.Row;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@Builder(toBuilder = true)
public class UserEntity {

  int id;
  long accountId;
  String name;
  String createdAt;
  String updatedAt;


  public static UserEntity fromDbRow(Row row) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    return builder()
      .id(row.getInteger("id"))
      .accountId(row.getLong("account_id"))
      .name(row.getString("name"))
      .createdAt(formatter.format(row.getLocalDateTime(("created_at"))))
      .updatedAt(formatter.format(row.getLocalDateTime(("updated_at"))))
      .build();
  }
}
