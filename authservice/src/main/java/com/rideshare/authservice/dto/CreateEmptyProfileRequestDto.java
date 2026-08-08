package com.rideshare.authservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateEmptyProfileRequestDto {
    private UUID authUserId;
    private String email;
}
