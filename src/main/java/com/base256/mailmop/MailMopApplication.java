package com.base256.mailmop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Application entry point.
//
// @SpringBootApplication turns on component scanning for everything under
// com.base256.mailmop plus Spring Boot's auto-configuration.
//
// @EnableScheduling lets the timed background jobs added in later steps
// (mailbox scans, OAuth token refresh) run on a fixed schedule.
@SpringBootApplication
@EnableScheduling
public class MailMopApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailMopApplication.class, args);
    }
}
