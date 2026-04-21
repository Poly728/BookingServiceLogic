package org.example.bookingservicelogic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration for API documentation.
 *
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    /**
     * Configures OpenAPI documentation.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Booking Service Logic API")
                        .version("1.0")
                        .description("REST API for Hotel Booking System - Backend Logic Microservice (MS1). " +
                                "This service handles all business logic, data persistence, and provides " +
                                "JSON responses for the frontend microservice.")
                        .contact(new Contact()
                                .name("Booking Service Team")
                                .email("support@bookingservice.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + "/api/v1")
                                .description("Development Server")
                ));
    }
}
