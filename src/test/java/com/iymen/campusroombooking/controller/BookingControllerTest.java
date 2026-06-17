package com.iymen.campusroombooking.controller;

import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Test
        public void createBooking_withValidRequest_returnsCreated() throws Exception {
                String requestJson = """
                                {
                                  "roomId": 3,
                                  "bookedBy": "John Doe",
                                  "date": "2026-06-10",
                                  "startTime": "10:00",
                                  "endTime": "11:00"
                                }
                                """;
                mockMvc.perform(post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.roomId").value(3))
                                .andExpect(jsonPath("$.bookedBy").value("John Doe"))
                                .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        public void createBooking_withMissingDate_returnsBadRequestAndErrorMessage() throws Exception {
                String requestJson = """
                                {
                                  "roomId": 3,
                                  "bookedBy": "John Doe",
                                  "startTime": "10:00",
                                  "endTime": "11:00"
                                }
                                """;
                mockMvc.perform(post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("date must not be null"));
        }

        @Test
        public void createBooking_withMalformedRequest_returnsBadRequestAndErrorMessage() throws Exception {
                String requestJson = """
                                {
                                  "roomId": 3,
                                  "bookedBy": "John Doe"
                                """;
                mockMvc.perform(post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Malformed request body."));
        }

        @Test
        public void createBooking_withConflict_returnsConflictAndErrorMessage() throws Exception {
                String requestJson = """
                                {
                                  "roomId": 1,
                                  "bookedBy": "John Doe",
                                  "date": "2026-06-20",
                                  "startTime": "10:30",
                                  "endTime": "11:30"
                                }
                                """;
                mockMvc.perform(post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message").value("Room is already booked for that time."));
        }

        @Test
        public void createBooking_withBlankBookedBy_returnsBadRequest() throws Exception {
                String requestJson = """
                                {
                                  "roomId": 2,
                                  "bookedBy": " ",
                                  "date": "2026-06-11",
                                  "startTime":"10:00",
                                  "endTime": "11:00"
                                }
                                """;
                mockMvc.perform(post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("bookedBy must not be blank"));
        }

        @Test
        public void getBookings_withInvalidStatus_returnsBadRequestAndErrorMessage() throws Exception {
                mockMvc.perform(get("/api/bookings?status=BROKEN"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Invalid value for status."));
        }

        @Test
        public void getRoom_withMissingRoom_returnsNotFoundAndErrorMessage() throws Exception {
                mockMvc.perform(get("/api/rooms/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Room not found."));
        }

        @Test
        public void getRooms_withNegativeMinCapacity_returnsBadRequestAndErrorMessage() throws Exception {
                mockMvc.perform(get("/api/rooms?minCapacity=-1"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("minCapacity cannot be negative."));
        }

        @Test
        public void getAvailableRooms_withInvalidTime_returnsBadRequestAndErrorMessage() throws Exception {
                mockMvc.perform(get(
                                "/api/rooms/available?date=2026-06-20&startTime=11:30&endTime=11:00"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message")
                                                .value("Start time must be before end time."));
        }

        @Test
        public void getAvailableRooms_withMissingDate_returnsBadRequestAndErrorMessage() throws Exception {
                mockMvc.perform(get("/api/rooms/available?startTime=10:00&endTime=11:00"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Missing required parameter: date."));
        }

        @Test
        public void getAvailableRooms_withInvalidDate_returnsBadRequestAndErrorMessage() throws Exception {
                mockMvc.perform(get(
                                "/api/rooms/available?date=whatever&startTime=10:00&endTime=11:00"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Invalid value for date."));
        }

}
