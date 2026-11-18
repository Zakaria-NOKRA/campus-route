package com.campusroute.campus_route_backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.campusroute.campus_route_backend.dto.PlanDto;
import com.campusroute.campus_route_backend.dto.PlanRoomDto;
import com.campusroute.campus_route_backend.dto.RouteNodeDto;

// Full context: GraphDataLoader fills the H2 database at startup (77 rooms, 251 nodes, 264 edges)
@SpringBootTest
class PlanServiceTest {

    @Autowired PlanService service;

    @Test
    void planHasAllTheCampusData() {
        PlanDto plan = service.getPlan();

        assertThat(plan.rooms()).hasSize(77);
        assertThat(plan.nodes()).hasSize(251);
        assertThat(plan.edges()).hasSize(264);
        assertThat(plan.floors()).containsExactly(0, 1, 2);
        assertThat(plan.scaleMetersPerPixel()).isEqualTo(RouteConstants.METERS_PER_PIXEL);
    }

    @Test
    void roomsAreSortedByCode() {
        assertThat(service.getPlan().rooms()).extracting(PlanRoomDto::code).isSorted();
    }

    @Test
    void roomHasItsRectangleAndItsDoorIsItsNode() {
        PlanDto plan = service.getPlan();
        PlanRoomDto room = plan.rooms().stream().filter(r -> r.code().equals("A204")).findFirst().orElseThrow();
        RouteNodeDto node = plan.nodes().stream().filter(n -> n.id().equals("A204")).findFirst().orElseThrow();

        assertThat(room.building()).isEqualTo("A");
        assertThat(room.floor()).isEqualTo(2);
        assertThat(room.tag()).isEqualTo("TD");
        assertThat(room.width()).isEqualTo(230);
        assertThat(room.height()).isEqualTo(100);
        assertThat(room.doorX()).isEqualTo(node.x());
        assertThat(room.doorY()).isEqualTo(node.y());
    }

    @Test
    void edgeKindsAreWalkStairsOrElevator() {
        assertThat(service.getPlan().edges())
                .allSatisfy(e -> assertThat(e.kind()).isIn("WALK", "STAIRS", "ELEVATOR"));
    }
}
