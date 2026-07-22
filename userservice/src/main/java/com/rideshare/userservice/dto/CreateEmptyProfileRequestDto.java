package com.rideshare.userservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateEmptyProfileRequestDto {
    private UUID authUserId;
}
