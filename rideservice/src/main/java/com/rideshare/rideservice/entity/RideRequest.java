package com.rideshare.rideservice.entity;


import com.rideshare.rideservice.enums.RideRequestStatus;
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
    private Integer Id;

    @Column(nullable = false)
    private Integer rideId;

    @Column(nullable = false)
    private UUID passengerAuthUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideRequestStatus status;

    private LocalDateTime requestedAt;

    @PrePersist
    public void onCreate() {
        requestedAt = LocalDateTime.now();
        status = RideRequestStatus.PENDING;
    }
}
