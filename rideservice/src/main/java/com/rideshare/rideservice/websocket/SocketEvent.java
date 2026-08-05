package com.rideshare.rideservice.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketEvent<T> {

    private String type;
    private T payload;

}