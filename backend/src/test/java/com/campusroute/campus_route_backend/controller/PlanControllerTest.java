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
class PlanControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void planOfABuildingReturnsItsRoomsNodesAndEdges() throws Exception {
        mvc.perform(get("/api/plan").param("building", "A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.building").value("A"))
                .andExpect(jsonPath("$.name").value("Bâtiment A – Sciences"))
                .andExpect(jsonPath("$.scaleMetersPerPixel").value(0.07))
                .andExpect(jsonPath("$.width").value(1000))
                .andExpect(jsonPath("$.height").value(800))
                .andExpect(jsonPath("$.outline").isString())
                .andExpect(jsonPath("$.floors.length()").value(3))
                .andExpect(jsonPath("$.rooms.length()").value(32))
                .andExpect(jsonPath("$.nodes.length()").value(97))
                .andExpect(jsonPath("$.edges.length()").value(100));
    }

    @Test
    void roomHasPlanFieldsAndNoNestedNode() throws Exception {
        mvc.perform(get("/api/plan").param("building", "A"))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].name").value("Salle de TD A204"))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].tag").value("TD"))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].building").value("A"))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].floor").value(2))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].x").value(750))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].y").value(20))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].width").value(230))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].height").value(100))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].doorX").value(865))
                .andExpect(jsonPath("$.rooms[?(@.code == 'A204')].doorY").value(120))
                .andExpect(jsonPath("$.rooms[0].slot").doesNotExist())
                .andExpect(jsonPath("$.rooms[0].node").doesNotExist());
    }

    @Test
    void outdoorPlanHasTheEntrance() throws Exception {
        mvc.perform(get("/api/plan").param("building", "ext"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rooms.length()").value(0))
                .andExpect(jsonPath("$.nodes[?(@.id == 'ENTREE')].type").value("ENTRANCE"))
                .andExpect(jsonPath("$.nodes[?(@.id == 'EXT-P2')].type").value("OUTDOOR"));
    }

    @Test
    void edgeHasFromToAndKind() throws Exception {
        mvc.perform(get("/api/plan").param("building", "A"))
                .andExpect(jsonPath("$.edges[0].from").isString())
                .andExpect(jsonPath("$.edges[0].to").isString())
                .andExpect(jsonPath("$.edges[0].kind").value("WALK"));
    }

    @Test
    void missingBuildingGivesJson400() throws Exception {
        mvc.perform(get("/api/plan"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Paramètre obligatoire manquant : building"));
    }

    @Test
    void blankBuildingGivesJson400() throws Exception {
        mvc.perform(get("/api/plan").param("building", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Paramètre obligatoire vide : building"));
    }

    @Test
    void unknownBuildingGivesJson404() throws Exception {
        mvc.perform(get("/api/plan").param("building", "Z"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Bâtiment Z introuvable"));
    }
}
