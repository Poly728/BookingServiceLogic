package org.example.bookingservicelogic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bookingservicelogic.dto.request.AmenityCreateRequest;
import org.example.bookingservicelogic.dto.response.AmenityResponse;
import org.example.bookingservicelogic.service.AmenityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AmenityController.class)
@AutoConfigureMockMvc(addFilters = false)
class AmenityControllerWebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AmenityService amenityService;

    @Test
    @DisplayName("POST /amenities should create amenity and return 201")
    void createAmenityShouldReturnCreated() throws Exception {
        AmenityCreateRequest request = AmenityCreateRequest.builder()
                .name("WiFi")
                .description("Free internet")
                .icon("wifi")
                .build();

        AmenityResponse response = AmenityResponse.builder()
                .id(1L)
                .name("WiFi")
                .description("Free internet")
                .icon("wifi")
                .createdAt(LocalDateTime.now())
                .build();

        when(amenityService.createAmenity(any(AmenityCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("WiFi"));

        verify(amenityService).createAmenity(any(AmenityCreateRequest.class));
    }

    @Test
    @DisplayName("POST /amenities should return 400 for invalid body")
    void createAmenityShouldReturnBadRequestForInvalidInput() throws Exception {
        AmenityCreateRequest invalidRequest = AmenityCreateRequest.builder()
                .name("")
                .description("desc")
                .icon("icon")
                .build();

        mockMvc.perform(post("/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
