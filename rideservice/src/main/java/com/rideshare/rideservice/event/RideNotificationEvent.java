package com.rideshare.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideNotificationEvent {

    private String email;
    private long rideId;
    private String eventType;
}