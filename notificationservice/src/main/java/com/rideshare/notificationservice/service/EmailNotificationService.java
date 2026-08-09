package com.rideshare.notificationservice.service;

import com.rideshare.notificationservice.event.OtpNotificationEvent;
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
}