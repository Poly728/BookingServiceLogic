package org.example.bookingservicelogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Booking Service Logic microservice.
 * This is MS1 - the backend logic service that handles all business logic
 * and data persistence for the hotel booking system.
 *
 */
@SpringBootApplication
@Slf4j
public class BookingServiceLogicApplication {

    /**
     * Main entry point of the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceLogicApplication.class, args);
        log.info("==============================================");
        log.info("  Booking Service Logic (MS1) Started!");
        log.info("  Swagger UI: http://localhost:8081/api/v1/swagger-ui.html");
        log.info("==============================================");
    }



}
