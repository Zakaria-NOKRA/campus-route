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
    void planReturnsRoomsNodesAndEdges() throws Exception {
        mvc.perform(get("/api/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scaleMetersPerPixel").value(0.07))
                .andExpect(jsonPath("$.floors.length()").value(3))
                .andExpect(jsonPath("$.floors[0]").value(0))
                .andExpect(jsonPath("$.floors[2]").value(2))
                .andExpect(jsonPath("$.rooms.length()").value(27))
                .andExpect(jsonPath("$.nodes.length()").value(49))
                .andExpect(jsonPath("$.edges.length()").value(50));
    }

    @Test
    void roomHasPlanFieldsAndNoNestedNode() throws Exception {
        mvc.perform(get("/api/plan"))
                .andExpect(jsonPath("$.rooms[?(@.code == 'B204')].name").value("Salle de TD B204"))
                .andExpect(jsonPath("$.rooms[?(@.code == 'B204')].tag").value("TD"))
                .andExpect(jsonPath("$.rooms[?(@.code == 'B204')].floor").value(2))
                .andExpect(jsonPath("$.rooms[?(@.code == 'B204')].slot").value(3))
                .andExpect(jsonPath("$.rooms[?(@.code == 'B204')].top").value(true))
                .andExpect(jsonPath("$.rooms[?(@.code == 'B204')].x").value(536))
                .andExpect(jsonPath("$.rooms[?(@.code == 'B204')].y").value(115))
                .andExpect(jsonPath("$.rooms[0].node").doesNotExist());
    }

    @Test
    void edgeHasFromToAndKind() throws Exception {
        mvc.perform(get("/api/plan"))
                .andExpect(jsonPath("$.edges[0].from").isString())
                .andExpect(jsonPath("$.edges[0].to").isString())
                .andExpect(jsonPath("$.edges[0].kind").value("WALK"));
    }
}
