package com.rideshare.rideservice.websocket;

import com.rideshare.rideservice.dto.LiveLocationDto;
import lombok.Data;

@Data
public class RideLiveLocation {

    private LiveLocationDto driverLocation;

    private LiveLocationDto passengerLocation;

}