package com.rideshare.userservice.dto;

import com.rideshare.userservice.enums.UserMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserModeRequestDto {

    @NotNull
    private UserMode userMode;

}