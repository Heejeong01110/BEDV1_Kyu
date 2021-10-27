package org.prgrms.kyu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prgrms.kyu.entity.Store;
import org.prgrms.kyu.entity.User;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreCreateRequest {
  String name;
  String telephone;
  String description;
  String location;
  Long userId;

  public Store convertToStore(User user){
    return Store.builder()
        .name(this.name)
        .telephone(this.telephone)
        .description(this.description)
        .location(this.location)
        .user(user)
        .build();
  }
}
