package com.dharshi.orderservice.feigns;

import com.dharshi.orderservice.dtos.ApiResponseDto;
import com.dharshi.orderservice.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("USER-SERVICE")
public interface UserService {

    @GetMapping("/purely/user/get/byId")
    ResponseEntity<ApiResponseDto<UserDto>> getUserById(@RequestParam String id);

}
