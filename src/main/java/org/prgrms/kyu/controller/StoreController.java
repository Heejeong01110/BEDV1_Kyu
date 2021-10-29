package org.prgrms.kyu.controller;

import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.prgrms.kyu.dto.JoinRequest;
import org.prgrms.kyu.dto.StoreCreateRequest;
import org.prgrms.kyu.service.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class StoreController {
  private final StoreService storeService;

  @PostMapping("/store")
  public String signUp(@ModelAttribute("storeCreateForm") StoreCreateRequest storeCreateRequest)
      throws AuthenticationException {
    storeService.save(storeCreateRequest);
    return "redirect:/";
  }
}
