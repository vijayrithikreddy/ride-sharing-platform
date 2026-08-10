package com.rideshare.notificationservice.service;

import com.rideshare.notificationservice.event.OtpNotificationEvent;
import com.rideshare.notificationservice.event.RideNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(OtpNotificationEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("rith89448@gmail.com");
        message.setTo(event.getEmail());

        message.setSubject("RideShare - OTP Verification");

        message.setText(
                "Hello,\n\n" +
                        "Your RideShare verification OTP is: "
                        + event.getOtp()
                        + "\n\n" +
                        "This OTP is valid for 10 minutes.\n\n" +
                        "Please do not share this OTP with anyone.\n\n" +
                        "Regards,\n" +
                        "RideShare Team"
        );

        mailSender.send(message);
    }

    public void sendRideNotification(
            RideNotificationEvent event
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom("rith89448@gmail.com");
        message.setTo(event.getEmail());

        if ("RIDE_STARTED".equals(event.getEventType())) {

            message.setSubject(
                    "RideShare - Ride Started"
            );

            message.setText(
                    "Hello,\n\n" +
                            "Your RideShare ride #" +
                            event.getRideId() +
                            " has started.\n\n" +
                            "Have a safe journey!\n\n" +
                            "Regards,\n" +
                            "RideShare Team"
            );

        } else if ("RIDE_COMPLETED".equals(event.getEventType())) {

            message.setSubject(
                    "RideShare - Ride Completed"
            );

            message.setText(
                    "Hello,\n\n" +
                            "Your RideShare ride #" +
                            event.getRideId() +
                            " has been completed.\n\n" +
                            "Thank you for using RideShare!\n\n" +
                            "Regards,\n" +
                            "RideShare Team"
            );

        } else {
            throw new IllegalArgumentException(
                    "Unknown ride notification type: "
                            + event.getEventType()
            );
        }

        mailSender.send(message);
    }
}