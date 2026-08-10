package com.rideshare.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideNotificationEvent {

    private String email;
    private Long rideId;
    private String eventType;
}