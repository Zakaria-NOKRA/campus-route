package com.campusroute.campus_route_backend.config;

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
class OpenApiTest {

    @Autowired MockMvc mvc;

    @Test
    void apiDocsListAllEndpoints() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Campus Route API"))
                .andExpect(jsonPath("$.servers[0].url").value("http://localhost:8080"))
                .andExpect(jsonPath("$.paths['/api/rooms/search']").exists())
                .andExpect(jsonPath("$.paths['/api/rooms/{code}']").exists())
                .andExpect(jsonPath("$.paths['/api/route']").exists())
                .andExpect(jsonPath("$.paths['/api/plan']").exists())
                .andExpect(jsonPath("$.paths['/api/buildings']").exists());
    }

    @Test
    void errorSchemaIsDocumented() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.components.schemas.ErrorResponse.properties.status").exists())
                .andExpect(jsonPath("$.components.schemas.ErrorResponse.properties.message").exists());
    }
}
