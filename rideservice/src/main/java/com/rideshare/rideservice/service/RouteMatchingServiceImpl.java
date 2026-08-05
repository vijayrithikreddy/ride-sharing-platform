package com.rideshare.rideservice.service;

import com.google.maps.model.LatLng;
import com.rideshare.rideservice.dto.LocationDto;
import com.rideshare.rideservice.dto.RideSearchResponseDto;
import com.rideshare.rideservice.dto.SearchRideRequestDto;
import com.rideshare.rideservice.dto.UserSummaryDto;
import com.rideshare.rideservice.entity.Ride;
import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.feign.UserServiceClient;
import com.rideshare.rideservice.model.Location;
import com.rideshare.rideservice.model.RideMatch;
import com.rideshare.rideservice.repository.RideRepository;
import com.rideshare.rideservice.util.GeoUtils;
import com.rideshare.rideservice.util.PolylineUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteMatchingServiceImpl implements RouteMatchingService {

    private static final double SOURCE_RADIUS_METERS = 5000.0;
    private static final double MATCH_DISTANCE_THRESHOLD = 100.0;
    private static final double MATCH_THRESHOLD_PERCENT = 50.0;

    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;
    private final UserServiceClient userServiceClient;

    @Override
    public List<RideSearchResponseDto> findMatchingRides(SearchRideRequestDto request) {

        LocalDateTime departureTime = request.getDepartureTime();

        // Step 1 : Fetch only nearby departure time rides
        List<Ride> availableRides =
                rideRepository.findByStatusAndDepartureTimeBetween(
                        RideStatus.AVAILABLE,
                        departureTime.minusMinutes(30),
                        departureTime.plusMinutes(30)
                );
        log.info("{} " ,availableRides.size());

        // Step 2 : Pickup location filter
        List<Ride> candidateRides = availableRides.stream()
                .filter(ride -> isSourceNearby(
                        ride.getSource(),
                        request.getSource()))
                .toList();
        log.info("{} " ,candidateRides.size());

        // Step 3 : Route matching
        List<RideMatch> matchedRides =
                getMatchingRides(
                        candidateRides,
                        request.getPassengerEncodedPolyline());
        log.info("{} " ,matchedRides.size());

        if (matchedRides.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> driverIds = matchedRides.stream()
                .map(match -> match.getRide().getDriverAuthUserId())
                .distinct()
                .toList();

        List<UserSummaryDto> driverSummaries = userServiceClient.getUserSummaries(driverIds);
        Map<UUID, UserSummaryDto> driverMap =
                driverSummaries.stream()
                        .collect(Collectors.toMap(
                                UserSummaryDto::getAuthUserId,
                                Function.identity()
                        ));

        // Step 4 : Best match first
        matchedRides.sort(
                Comparator.comparingDouble(RideMatch::getMatchPercentage)
                        .reversed());

        // Step 5 : Convert to response DTO
        return matchedRides.stream()
                .map(match -> {

                    UserSummaryDto driver =
                            driverMap.get(
                                    match.getRide().getDriverAuthUserId());

                    return RideSearchResponseDto.builder()
                            .rideId(match.getRide().getRideId())
                            .source(modelMapper.map(
                                    match.getRide().getSource(),
                                    LocationDto.class))
                            .destination(modelMapper.map(
                                    match.getRide().getDestination(),
                                    LocationDto.class))
                            .encodedPolyline(match.getRide().getEncodedPolyline())
                            .departureTime(match.getRide().getDepartureTime())
                            .price(match.getRide().getRidePrice())
                            .matchPercentage(match.getMatchPercentage())
                            .driverProfile(driver)
                            .build();

                })
                .toList();
    }

    private boolean isSourceNearby(
            Location riderSource,
            LocationDto passengerSource) {

        double distance = GeoUtils.haversineDistance(
                riderSource.getLatitude(),
                riderSource.getLongitude(),
                passengerSource.getLatitude(),
                passengerSource.getLongitude()
        );

        return distance <= SOURCE_RADIUS_METERS;
    }

    private List<RideMatch> getMatchingRides(
            List<Ride> candidateRides,
            String passengerEncodedPolyline) {

        List<LatLng> passengerRoute =
                PolylineUtils.decode(passengerEncodedPolyline);
        log.info("Passenger Route Points: {}", passengerRoute.size());
        log.info("Passenger First Point: {}", passengerRoute.get(0));



        List<RideMatch> matchedRides = new ArrayList<>();


        for (Ride ride : candidateRides) {

            List<LatLng> riderRoute =
                    PolylineUtils.decode(ride.getEncodedPolyline());
            log.info("Rider Route Points: {}", riderRoute.size());
            log.info("Rider First Point: {}", riderRoute.get(0));

            double matchPercentage =
                    calculateMatchPercentage(
                            passengerRoute,
                            riderRoute);
            log.info("Ride {} -> Match {}%",
                    ride.getRideId(),
                    matchPercentage);

            if (matchPercentage >= MATCH_THRESHOLD_PERCENT) {

                matchedRides.add(
                        RideMatch.builder()
                                .ride(ride)
                                .matchPercentage(matchPercentage)
                                .build());

            }
        }

        return matchedRides;
    }

    private double calculateMatchPercentage(
            List<LatLng> passengerRoute,
            List<LatLng> riderRoute) {

        if (passengerRoute.isEmpty() || riderRoute.isEmpty()) {
            return 0.0;
        }

        int matchedPoints = 0;

        for (LatLng passengerPoint : passengerRoute) {

            boolean matched = false;

            for (LatLng riderPoint : riderRoute) {

                double distance = GeoUtils.haversineDistance(
                        passengerPoint.lat,
                        passengerPoint.lng,
                        riderPoint.lat,
                        riderPoint.lng
                );

                if (distance <= MATCH_DISTANCE_THRESHOLD) {
                    matched = true;
                    break;
                }
            }

            if (matched) {
                matchedPoints++;
            }
        }

        log.info("Matched Points = {}", matchedPoints);

        return matchedPoints * 100.0 / passengerRoute.size();
    }
}