package com.rideshare.rideservice.websocket;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RideLocationStore {

    private final ConcurrentHashMap<Integer, RideLiveLocation> locations =
            new ConcurrentHashMap<>();

    public void updateDriverLocation(
            Integer rideId,
            com.rideshare.rideservice.dto.LiveLocationDto location
    ) {

        RideLiveLocation rideLocation =
                locations.computeIfAbsent(
                        rideId,
                        id -> new RideLiveLocation()
                );

        rideLocation.setDriverLocation(location);
    }

    public void updatePassengerLocation(
            Integer rideId,
            com.rideshare.rideservice.dto.LiveLocationDto location
    ) {

        RideLiveLocation rideLocation =
                locations.computeIfAbsent(
                        rideId,
                        id -> new RideLiveLocation()
                );

        rideLocation.setPassengerLocation(location);
    }

    public RideLiveLocation getLocation(
            Integer rideId
    ) {
        return locations.get(rideId);
    }

    public void remove(Integer rideId) {
        locations.remove(rideId);
    }

}