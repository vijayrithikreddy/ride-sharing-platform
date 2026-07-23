package com.rideshare.userservice.entity;

import com.rideshare.userservice.enums.VehicleType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    private String color;

    @OneToOne
    @JoinColumn(
            name = "user_profile_id",
            nullable = false,
            unique = true
    )
    private UserProfile userProfile;
}