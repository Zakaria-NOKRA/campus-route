package com.campusroute.campus_route_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.campusroute.campus_route_backend.dto.PlanDto;
import com.campusroute.campus_route_backend.dto.PlanRoomDto;
import com.campusroute.campus_route_backend.dto.RouteNodeDto;

// Full context: GraphDataLoader fills the H2 database with the campus at startup.
// Building A: 32 rooms, 97 nodes, 100 edges inside it (counts printed by tools/campus/generate.mjs)
@SpringBootTest
class PlanServiceTest {

    @Autowired PlanService service;

    @Test
    void planOfBuildingAHasOnlyItsOwnData() {
        PlanDto plan = service.getPlan("A");

        assertThat(plan.building()).isEqualTo("A");
        assertThat(plan.name()).isEqualTo("Bâtiment A – Sciences");
        assertThat(plan.width()).isEqualTo(1000);
        assertThat(plan.height()).isEqualTo(800);
        assertThat(plan.outline()).startsWith("0,0 1000,0");
        assertThat(plan.floors()).containsExactly(0, 1, 2);
        assertThat(plan.scaleMetersPerPixel()).isEqualTo(RouteConstants.METERS_PER_PIXEL);

        assertThat(plan.rooms()).hasSize(32).allSatisfy(r -> assertThat(r.building()).isEqualTo("A"));
        assertThat(plan.nodes()).hasSize(97).allSatisfy(n -> assertThat(n.building()).isEqualTo("A"));
        assertThat(plan.edges()).hasSize(100);
    }

    @Test
    void edgesHaveBothEndsInTheBuilding() {
        PlanDto plan = service.getPlan("A");
        Set<String> ids = plan.nodes().stream().map(RouteNodeDto::id).collect(Collectors.toSet());

        // The door links A0-D1 -> EXT-PAW and A0-D2 -> EXT-PA are left out
        assertThat(plan.edges()).allSatisfy(e -> assertThat(ids).contains(e.from(), e.to()));
    }

    @Test
    void codeIsTrimmedAndUppercased() {
        PlanDto plan = service.getPlan(" b ");
        assertThat(plan.building()).isEqualTo("B");
        assertThat(plan.floors()).containsExactly(0, 1);
        assertThat(plan.rooms()).hasSize(16);
    }

    @Test
    void outdoorPlanHasNoRooms() {
        PlanDto plan = service.getPlan("EXT");
        assertThat(plan.rooms()).isEmpty();
        assertThat(plan.nodes()).hasSize(14);
        assertThat(plan.edges()).hasSize(14);
        assertThat(plan.floors()).containsExactly(0);
    }

    @Test
    void roomsAreSortedByCode() {
        assertThat(service.getPlan("C").rooms()).extracting(PlanRoomDto::code).isSorted();
    }

    @Test
    void roomHasItsRectangleAndItsDoorIsItsNode() {
        PlanDto plan = service.getPlan("A");
        PlanRoomDto room = plan.rooms().stream().filter(r -> r.code().equals("A204")).findFirst().orElseThrow();
        RouteNodeDto node = plan.nodes().stream().filter(n -> n.id().equals("A204")).findFirst().orElseThrow();

        assertThat(room.floor()).isEqualTo(2);
        assertThat(room.tag()).isEqualTo("TD");
        assertThat(room.width()).isEqualTo(230);
        assertThat(room.height()).isEqualTo(100);
        assertThat(room.doorX()).isEqualTo(node.x());
        assertThat(room.doorY()).isEqualTo(node.y());
    }

    @Test
    void edgeKindsAreWalkStairsOrElevator() {
        assertThat(service.getPlan("A").edges())
                .allSatisfy(e -> assertThat(e.kind()).isIn("WALK", "STAIRS", "ELEVATOR"));
    }

    @Test
    void unknownBuildingThrows() {
        assertThatThrownBy(() -> service.getPlan("z"))
                .isInstanceOf(BuildingNotFoundException.class)
                .hasMessage("Bâtiment Z introuvable");
    }
}
