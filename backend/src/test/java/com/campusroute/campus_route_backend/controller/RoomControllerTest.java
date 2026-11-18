package com.campusroute.campus_route_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void searchReturnsMatchingRooms() throws Exception {
        mvc.perform(get("/api/rooms/search").param("q", "turing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("B001"))
                .andExpect(jsonPath("$[0].floor").value(0));
    }

    @Test
    void searchWithoutQueryReturnsEmptyArray() throws Exception {
        mvc.perform(get("/api/rooms/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchReturnsAtMostEightRooms() throws Exception {
        mvc.perform(get("/api/rooms/search").param("q", "a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0].code").value("A001"));
    }

    @Test
    void getReturnsTheRoomSheet() throws Exception {
        mvc.perform(get("/api/rooms/A204"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("A204"))
                .andExpect(jsonPath("$.name").value("Salle de TD A204"))
                .andExpect(jsonPath("$.building").value("A"))
                .andExpect(jsonPath("$.type").value("Salle de TD"))
                .andExpect(jsonPath("$.floor").value(2))
                .andExpect(jsonPath("$.capacity").value(30))
                .andExpect(jsonPath("$.equipment.length()").value(2))
                .andExpect(jsonPath("$.node").doesNotExist());
    }

    @Test
    void getUnknownRoomReturns404WithJsonMessage() throws Exception {
        mvc.perform(get("/api/rooms/B999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Salle B999 introuvable"));
    }
}
