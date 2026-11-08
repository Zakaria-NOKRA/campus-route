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

// Full context: the real graph is loaded in H2. Expected values come from the prototype.
@SpringBootTest
class RouteServiceTest {

    @Autowired RouteService service;

    private List<String> ids(RouteDto route) {
        return route.path().stream().map(RouteNodeDto::id).toList();
    }

    @Test
    void entranceToB204GoesByElevator() {
        RouteDto route = service.findRoute("ENTREE", "B204", false);
        assertThat(route.distanceMeters()).isCloseTo(54.95, within(0.01));
        assertThat(route.durationSeconds()).isCloseTo(92.3, within(0.1));
        assertThat(ids(route)).containsExactly(
                "ENTREE", "C0_2", "C0_3", "C0_4", "E0", "E1", "E2", "C2_4", "C2_3", "B204");
        assertThat(route.steps()).hasSize(7);
    }

    @Test
    void entranceToB204IsTheSameWhenAccessible() {
        RouteDto normal = service.findRoute("ENTREE", "B204", false);
        RouteDto accessible = service.findRoute("ENTREE", "B204", true);
        assertThat(ids(accessible)).isEqualTo(ids(normal));
        assertThat(accessible.distanceMeters()).isEqualTo(normal.distanceMeters());
    }

    @Test
    void otherReferenceRoutes() {
        RouteDto toB105 = service.findRoute("ENTREE", "B105", false);
        assertThat(toB105.distanceMeters()).isCloseTo(45.43, within(0.01));
        assertThat(toB105.durationSeconds()).isCloseTo(59.9, within(0.1));

        RouteDto toB009 = service.findRoute("B204", "B009", false);
        assertThat(toB009.distanceMeters()).isCloseTo(34.86, within(0.01));
        assertThat(toB009.durationSeconds()).isCloseTo(76.8, within(0.1));

        RouteDto toB207 = service.findRoute("B108", "B207", false);
        assertThat(toB207.distanceMeters()).isCloseTo(63.42, within(0.01));
        assertThat(toB207.durationSeconds()).isCloseTo(73.8, within(0.1));
    }

    @Test
    void b101ToB201TakesTheStairsByDefault() {
        RouteDto route = service.findRoute("B101", "B201", false);
        assertThat(route.distanceMeters()).isCloseTo(25.34, within(0.01));
        assertThat(route.durationSeconds()).isCloseTo(31.5, within(0.1));
        assertThat(ids(route)).containsExactly("B101", "C1_0", "S1", "S2", "C2_0", "B201");
    }

    @Test
    void b101ToB201AccessibleAvoidsStairs() {
        RouteDto route = service.findRoute("B101", "B201", true);
        assertThat(route.distanceMeters()).isCloseTo(101.50, within(0.01));
        assertThat(route.durationSeconds()).isCloseTo(103.1, within(0.1));
        assertThat(ids(route)).noneMatch(id -> id.startsWith("S"));
        assertThat(ids(route)).contains("E1", "E2");
    }

    @Test
    void sameStartAndDestinationGivesAnEmptyRoute() {
        RouteDto route = service.findRoute("B101", "B101", false);
        assertThat(route.distanceMeters()).isZero();
        assertThat(route.durationSeconds()).isZero();
        assertThat(ids(route)).containsExactly("B101");
        assertThat(route.steps()).hasSize(2); // departure + arrival
    }

    @Test
    void idsAreTrimmedAndUppercased() {
        RouteDto route = service.findRoute("  entree ", "b105", false);
        assertThat(route.distanceMeters()).isCloseTo(45.43, within(0.01));
    }

    @Test
    void unknownNodeThrows() {
        assertThatThrownBy(() -> service.findRoute("ZZZ", "B204", false))
                .isInstanceOf(NodeNotFoundException.class)
                .hasMessage("Point ZZZ introuvable");
        assertThatThrownBy(() -> service.findRoute("B204", "zzz", false))
                .isInstanceOf(NodeNotFoundException.class)
                .hasMessage("Point ZZZ introuvable");
    }

    @Test
    void lastStepNamesTheDestinationRoom() {
        RouteDto route = service.findRoute("ENTREE", "B204", false);
        var last = route.steps().get(route.steps().size() - 1);
        assertThat(last.text()).isEqualTo("Vous êtes arrivé : B204");
        assertThat(last.sub()).isEqualTo("Salle de TD B204 · 2e étage");
    }
}
