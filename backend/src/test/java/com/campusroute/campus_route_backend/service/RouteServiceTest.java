package com.campusroute.campus_route_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.campusroute.campus_route_backend.dto.RouteDto;
import com.campusroute.campus_route_backend.dto.RouteNodeDto;

// Full context: the real campus graph is loaded in H2.
// Distance = WALK edges only x 0.07 m; duration = distance / 1.3 m/s + 12 s per stairs edge + 25 s per elevator edge.
@SpringBootTest
class RouteServiceTest {

    @Autowired RouteService service;

    private List<String> ids(RouteDto route) {
        return route.path().stream().map(RouteNodeDto::id).toList();
    }

    @Test
    void entranceToA204CrossesBuildingBAndTheCourtyard() {
        RouteDto route = service.findRoute("ENTREE", "A204", false);
        assertThat(route.distanceMeters()).isCloseTo(181.65, within(0.01));
        assertThat(route.durationSeconds()).isCloseTo(189.7, within(0.1));
        assertThat(ids(route)).startsWith("ENTREE", "EXT-P1", "B0-D1")  // into building B by its main door
                .contains("B0-D2", "EXT-P2", "A0-D2")                    // out of B, courtyard, into A
                .contains("A0-E", "A1-E", "A2-E")                        // elevator up to floor 2
                .endsWith("A204");
    }

    @Test
    void entranceToA204IsTheSameWhenAccessible() {
        RouteDto normal = service.findRoute("ENTREE", "A204", false);
        RouteDto accessible = service.findRoute("ENTREE", "A204", true);
        assertThat(ids(accessible)).isEqualTo(ids(normal));
        assertThat(accessible.distanceMeters()).isEqualTo(normal.distanceMeters());
    }

    @Test
    void a101ToA201TakesTheStairsByDefault() {
        RouteDto route = service.findRoute("A101", "A201", false);
        assertThat(route.distanceMeters()).isCloseTo(18.9, within(0.01));
        assertThat(route.durationSeconds()).isCloseTo(26.5, within(0.1));
        assertThat(ids(route)).containsExactly("A101", "A101_C", "A1-S1_C", "A1-S1", "A2-S1", "A2-S1_C", "A201_C", "A201");
    }

    @Test
    void a101ToA201AccessibleTakesTheElevator() {
        RouteDto route = service.findRoute("A101", "A201", true);
        assertThat(route.distanceMeters()).isCloseTo(119.7, within(0.01));
        assertThat(route.durationSeconds()).isCloseTo(117.1, within(0.1));
        assertThat(ids(route)).noneMatch(id -> id.contains("-S"));
        assertThat(ids(route)).contains("A1-E", "A2-E");
    }

    @Test
    void inBuildingCTheElevatorIsFarFromTheEastWing() {
        RouteDto normal = service.findRoute("C0-D1", "C209", false);
        RouteDto accessible = service.findRoute("C0-D1", "C209", true);

        assertThat(normal.distanceMeters()).isCloseTo(87.36, within(0.01));
        assertThat(ids(normal)).contains("C0-S2", "C1-S2", "C2-S2"); // east stairs, next to C209
        assertThat(accessible.distanceMeters()).isCloseTo(159.74, within(0.01));
        assertThat(ids(accessible)).contains("C0-E", "C1-E", "C2-E"); // elevator at the end of the west wing
    }

    @Test
    void accessibleRouteToBuildingCTakesTheRampInsteadOfTheSteps() {
        RouteDto normal = service.findRoute("ENTREE", "C209", false);
        RouteDto accessible = service.findRoute("ENTREE", "C209", true);

        assertThat(normal.distanceMeters()).isCloseTo(210.56, within(0.01));
        assertThat(normal.durationSeconds()).isCloseTo(198.0, within(0.1));
        assertThat(ids(normal)).containsSequence("EXT-P4", "EXT-P5").doesNotContain("EXT-R1");

        assertThat(accessible.distanceMeters()).isCloseTo(331.94, within(0.01));
        assertThat(accessible.durationSeconds()).isCloseTo(305.3, within(0.1));
        assertThat(ids(accessible)).containsSequence("EXT-P4", "EXT-R1", "EXT-R2", "EXT-P5");
    }

    @Test
    void sameStartAndDestinationGivesAnEmptyRoute() {
        RouteDto route = service.findRoute("A101", "A101", false);
        assertThat(route.distanceMeters()).isZero();
        assertThat(route.durationSeconds()).isZero();
        assertThat(ids(route)).containsExactly("A101");
        assertThat(route.steps()).hasSize(2); // departure + arrival
    }

    @Test
    void idsAreTrimmedAndUppercased() {
        RouteDto route = service.findRoute("  entree ", "b001", false);
        assertThat(route.distanceMeters()).isCloseTo(50.4, within(0.01));
    }

    @Test
    void unknownNodeThrows() {
        assertThatThrownBy(() -> service.findRoute("ZZZ", "A204", false))
                .isInstanceOf(NodeNotFoundException.class)
                .hasMessage("Point ZZZ introuvable");
        assertThatThrownBy(() -> service.findRoute("A204", "zzz", false))
                .isInstanceOf(NodeNotFoundException.class)
                .hasMessage("Point ZZZ introuvable");
    }

    @Test
    void lastStepNamesTheDestinationRoom() {
        RouteDto route = service.findRoute("ENTREE", "A204", false);
        var last = route.steps().get(route.steps().size() - 1);
        assertThat(last.text()).isEqualTo("Vous êtes arrivé : A204");
        assertThat(last.sub()).isEqualTo("Salle de TD A204 · 2e étage");
    }
}
