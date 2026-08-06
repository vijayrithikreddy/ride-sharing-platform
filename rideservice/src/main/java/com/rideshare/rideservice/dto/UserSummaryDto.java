package com.rideshare.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDto {

    private UUID authUserId;

    private String firstName;

    private String lastName;
    private String phoneNumber;

    private String profilePictureUrl;

    private VehicleSummaryDto vehicle;
}