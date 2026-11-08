package com.campusroute.campus_route_backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigTest {

    private static final String ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    @Autowired MockMvc mvc;

    @Test
    void viteOriginIsAllowed() throws Exception {
        mvc.perform(get("/api/plan").header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string(ALLOW_ORIGIN, "http://localhost:5173"));
    }

    @Test
    void createReactAppOriginIsAllowed() throws Exception {
        mvc.perform(get("/api/rooms/search").param("q", "b204").header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string(ALLOW_ORIGIN, "http://localhost:3000"));
    }

    @Test
    void otherOriginIsRejected() throws Exception {
        mvc.perform(get("/api/plan").header("Origin", "http://evil.example"))
                .andExpect(header().doesNotExist(ALLOW_ORIGIN));
    }

    @Test
    void writeMethodsAreNotAllowedFromTheFront() throws Exception {
        mvc.perform(options("/api/plan")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(header().doesNotExist(ALLOW_ORIGIN));
    }

    @Test
    void corsIsNotOpenedOutsideApi() throws Exception {
        mvc.perform(get("/v3/api-docs").header("Origin", "http://localhost:5173"))
                .andExpect(header().doesNotExist(ALLOW_ORIGIN));
    }
}
