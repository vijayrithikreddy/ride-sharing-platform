package com.rideshare.authservice.feign;

import com.rideshare.authservice.dto.CreateEmptyProfileRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE")
public interface UserProfileClient {

    @PostMapping("/api/userprofiles")
    ResponseEntity<String> createEmptyUserProfile(@RequestBody CreateEmptyProfileRequestDto createEmptyProfileRequestDto);
}
