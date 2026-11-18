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
class BuildingControllerTest {

    @Autowired MockMvc mvc;

    @Test
    void listReturnsBuildingsWithFloorsAndOutlines() throws Exception {
        mvc.perform(get("/api/buildings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].code").value("A"))
                .andExpect(jsonPath("$[0].name").value("Bâtiment A – Sciences"))
                .andExpect(jsonPath("$[0].outdoor").value(false))
                .andExpect(jsonPath("$[0].floors.length()").value(3))
                .andExpect(jsonPath("$[0].width").value(1000))
                .andExpect(jsonPath("$[0].height").value(800))
                .andExpect(jsonPath("$[0].outline").isString())
                .andExpect(jsonPath("$[0].siteOutline").isString())
                .andExpect(jsonPath("$[1].code").value("B"))
                .andExpect(jsonPath("$[1].floors.length()").value(2))
                .andExpect(jsonPath("$[3].code").value("EXT"))
                .andExpect(jsonPath("$[3].outdoor").value(true));
    }
}
