package org.prgrms.kyu.controller;

import lombok.RequiredArgsConstructor;
import org.prgrms.kyu.dto.StoreCreateRequest;
import org.prgrms.kyu.dto.UserInfo;
import org.prgrms.kyu.entity.UserType;

import org.prgrms.kyu.service.SecurityService;
import org.prgrms.kyu.service.StoreService;
import org.prgrms.kyu.service.UserService;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.naming.AuthenticationException;

@Controller
@RequiredArgsConstructor
public class StoreController {
  private final StoreService storeService;
  private final SecurityService securityService;
  private final UserService userService;

  @GetMapping("/user/myStore")
  public String myStore(Model model, Authentication authentication) {
    if (!securityService.isAuthenticated()) return "redirect:/";
    UserType userType = userService.getUserType(
        ((UserDetails) authentication.getPrincipal()).getUsername());
    if(userType.equals(UserType.STORE_OWNER)){
      model.addAttribute("storeForm", new StoreCreateRequest());
      model.addAttribute("userInfo",
          userService.getUser(((UserDetails) authentication.getPrincipal()).getUsername()));

      return "/store/my-store";
    }else if(userType.equals(UserType.CUSTOMER)){
      return "/index";
    }
    return "/user/loginForm";
  }

  @GetMapping("/user/stores")
  public String getMyStores(Model model, Authentication authentication) {
    if (!securityService.isAuthenticated()) return "redirect:/";
    UserType userType = userService.getUserType(
        ((UserDetails) authentication.getPrincipal()).getUsername());
    if(userType.equals(UserType.STORE_OWNER)){
      UserInfo user = userService.getUser(
          ((UserDetails) authentication.getPrincipal()).getUsername());
      model.addAttribute("userInfo",
          user);
      model.addAttribute("stores", storeService.findByUserId(user.getId()));
      return "/store/myStoreListView";
    }else if(userType.equals(UserType.CUSTOMER)){
      return "/index";
    }
    return "/user/loginForm";
  }


  @PostMapping("/stores")
  public String signUp(@ModelAttribute("storeForm") StoreCreateRequest storeCreateRequest,
                       Authentication authentication)
      throws AuthenticationException {
    if (!securityService.isAuthenticated()) throw new AuthenticationException();
    final UserInfo userInfo = userService.getUser(authentication.getName());
    storeService.save(storeCreateRequest, userInfo.getId());
    return "redirect:/";
  }
}
