package com.rideshare.rideservice.feign;

import com.rideshare.rideservice.dto.UserSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @PostMapping("/api/userprofiles/summaries")
    List<UserSummaryDto> getUserSummaries(@RequestBody List<UUID> authUserIds);
}