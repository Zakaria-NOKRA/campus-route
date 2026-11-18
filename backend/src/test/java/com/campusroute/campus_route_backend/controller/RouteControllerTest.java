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
        mvc.perform(get("/api/route").param("from", "a101").param("to", " a201 "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceMeters").value(18.9))
                .andExpect(jsonPath("$.durationSeconds").value(26.5))
                .andExpect(jsonPath("$.path[0].id").value("A101"))
                .andExpect(jsonPath("$.path[0].type").value("ROOM"))
                .andExpect(jsonPath("$.path[3].id").value("A1-S1"))
                .andExpect(jsonPath("$.steps[0].text").value("Départ : A101"))
                .andExpect(jsonPath("$.steps[0].icon").exists())
                .andExpect(jsonPath("$.steps[0].floor").value(1));
    }

    @Test
    void accessibleTrueAvoidsStairs() throws Exception {
        mvc.perform(get("/api/route").param("from", "A101").param("to", "A201").param("accessible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceMeters").value(119.7))
                .andExpect(jsonPath("$.path[?(@.type == 'STAIRS')]").isEmpty());
    }

    @Test
    void sameStartAndDestinationIsValid() throws Exception {
        mvc.perform(get("/api/route").param("from", "A101").param("to", "A101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distanceMeters").value(0))
                .andExpect(jsonPath("$.path.length()").value(1));
    }

    @Test
    void unknownNodeGivesJson404() throws Exception {
        mvc.perform(get("/api/route").param("from", "ZZZ").param("to", "A204"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Point ZZZ introuvable"));
    }

    @Test
    void missingParameterGivesJson400() throws Exception {
        mvc.perform(get("/api/route").param("to", "A204"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Paramètre obligatoire manquant : from"));
    }

    @Test
    void emptyOrBlankParameterGivesJson400() throws Exception {
        mvc.perform(get("/api/route").param("from", "").param("to", "A204"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Paramètre obligatoire vide : from"));
        mvc.perform(get("/api/route").param("from", "A204").param("to", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
