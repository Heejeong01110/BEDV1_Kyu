package org.prgrms.kyu.controller.api;

import java.util.List;
import javassist.NotFoundException;
import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.prgrms.kyu.ApiResponse;
import org.prgrms.kyu.dto.StoreCreateRequest;
import org.prgrms.kyu.dto.StoreFindResponse;
import org.prgrms.kyu.service.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreRestController {

  private final StoreService storeService;

  @GetMapping("/stores")
  public ApiResponse<List<StoreFindResponse>> getAllStore(){
    return ApiResponse.ok(storeService.findAll());
  }

  @GetMapping("/stores/{id}")
  public ApiResponse<StoreFindResponse> getOneStore(@PathVariable Long id)
      throws NotFoundException {
    return ApiResponse.ok(storeService.findById(id));
  }

  @PostMapping("/stores")
  public ApiResponse<Long> saveStore(@RequestBody StoreCreateRequest storeCreateRequest)
      throws AuthenticationException {
    return ApiResponse.ok(storeService.save(storeCreateRequest));
  }



}
