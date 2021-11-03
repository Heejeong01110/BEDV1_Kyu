package org.prgrms.kyu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.prgrms.kyu.entity.Store;
import org.prgrms.kyu.entity.User;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreFindResponse {
  Long id;
  String name;
  String telephone;
  String description;
  String location;

  public StoreFindResponse(Store store) {
       this.id = store.getId();
       this.name = store.getName();
       this.telephone = store.getTelephone();
       this.description = store.getDescription();
       this.location = store.getLocation();
  }

  public Store convertToStore(){
    return Store.builder()
      .id(this.id)
      .name(this.name)
      .telephone(this.telephone)
      .description(this.description)
      .location(this.location)
      .build();
  }

}
