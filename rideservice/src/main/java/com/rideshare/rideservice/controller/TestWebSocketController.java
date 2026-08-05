package com.rideshare.rideservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/test/ws")
    public String sendMessage() {

        messagingTemplate.convertAndSend(
                "/topic/test",
                "Hello WebSocket"
        );

        return "Sent";
    }
}