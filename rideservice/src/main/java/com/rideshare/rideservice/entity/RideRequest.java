package com.rideshare.rideservice.entity;

import com.rideshare.rideservice.enums.RideRequestStatus;
import com.rideshare.rideservice.model.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ride_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @Column(nullable = false)
    private Integer rideId;

    @Column(nullable = false)
    private UUID passengerAuthUserId;

    // Passenger Route Snapshot

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",
                    column = @Column(name = "source_latitude")),
            @AttributeOverride(name = "longitude",
                    column = @Column(name = "source_longitude")),
            @AttributeOverride(name = "address",
                    column = @Column(name = "source_address"))
    })
    private Location source;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude",
                    column = @Column(name = "destination_latitude")),
            @AttributeOverride(name = "longitude",
                    column = @Column(name = "destination_longitude")),
            @AttributeOverride(name = "address",
                    column = @Column(name = "destination_address"))
    })
    private Location destination;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String passengerEncodedPolyline;

    @Column(nullable = false)
    private Double matchPercentage;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    // Request Details

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideRequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column
    private LocalDateTime acceptedAt;

    @Column
    private LocalDateTime rejectedAt;

    @Column
    private LocalDateTime cancelledAt;

    @PrePersist
    public void onCreate() {
        requestedAt = LocalDateTime.now();
        status = RideRequestStatus.PENDING;
    }
}