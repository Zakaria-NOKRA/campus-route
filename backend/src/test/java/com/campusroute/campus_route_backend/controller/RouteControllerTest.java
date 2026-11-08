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
class RouteControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void returnsRouteWithDefaultAccessibleFalse() throws Exception {
        mvc.perform(get("/api/route").param("from", "b101").param("to", " b201 "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceMeters").value(25.34))
                .andExpect(jsonPath("$.durationSeconds").value(31.5))
                .andExpect(jsonPath("$.path[0].id").value("B101"))
                .andExpect(jsonPath("$.path[0].type").value("ROOM"))
                .andExpect(jsonPath("$.path[2].id").value("S1"))
                .andExpect(jsonPath("$.steps[0].text").value("Départ : B101"))
                .andExpect(jsonPath("$.steps[0].icon").exists())
                .andExpect(jsonPath("$.steps[0].floor").value(1));
    }

    @Test
    void accessibleTrueAvoidsStairs() throws Exception {
        mvc.perform(get("/api/route").param("from", "B101").param("to", "B201").param("accessible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceMeters").value(101.5))
                .andExpect(jsonPath("$.path[?(@.id == 'S1')]").isEmpty());
    }

    @Test
    void sameStartAndDestinationIsValid() throws Exception {
        mvc.perform(get("/api/route").param("from", "B101").param("to", "B101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceMeters").value(0))
                .andExpect(jsonPath("$.path.length()").value(1));
    }

    @Test
    void unknownNodeGivesJson404() throws Exception {
        mvc.perform(get("/api/route").param("from", "ZZZ").param("to", "B204"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Point ZZZ introuvable"));
    }

    @Test
    void missingParameterGivesJson400() throws Exception {
        mvc.perform(get("/api/route").param("to", "B204"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Paramètre obligatoire manquant : from"));
    }

    @Test
    void emptyOrBlankParameterGivesJson400() throws Exception {
        mvc.perform(get("/api/route").param("from", "").param("to", "B204"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
        mvc.perform(get("/api/route").param("from", "B204").param("to", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
