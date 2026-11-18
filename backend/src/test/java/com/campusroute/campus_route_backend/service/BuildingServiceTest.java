package com.campusroute.campus_route_backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.campusroute.campus_route_backend.dto.BuildingDto;

// Full context: GraphDataLoader fills the H2 database with the campus at startup
@SpringBootTest
class BuildingServiceTest {

    @Autowired BuildingService service;

    @Test
    void listsTheThreeBuildingsThenTheOutdoorSpace() {
        assertThat(service.findAll()).extracting(BuildingDto::code).containsExactly("A", "B", "C", "EXT");
    }

    @Test
    void eachBuildingHasItsOwnFloors() {
        List<BuildingDto> buildings = service.findAll();
        assertThat(buildings.get(0).floors()).containsExactly(0, 1, 2); // A
        assertThat(buildings.get(1).floors()).containsExactly(0, 1);    // B
        assertThat(buildings.get(2).floors()).containsExactly(0, 1, 2); // C
        assertThat(buildings.get(3).floors()).containsExactly(0);       // EXT
    }

    @Test
    void outlinesArePlacedOnTheSitePlan() {
        BuildingDto a = service.findAll().get(0);
        assertThat(a.outdoor()).isFalse();
        assertThat(a.outline()).isEqualTo("0,0 1000,0 1000,800 700,800 700,300 0,300");
        // Same shape moved by the origin of A on the site plan (200, 200)
        assertThat(a.siteOutline()).isEqualTo("200,200 1200,200 1200,1000 900,1000 900,500 200,500");

        assertThat(service.findAll().get(3).outdoor()).isTrue();
    }
}
