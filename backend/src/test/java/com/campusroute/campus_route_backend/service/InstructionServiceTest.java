package com.campusroute.campus_route_backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.campusroute.campus_route_backend.domain.Node;
import com.campusroute.campus_route_backend.domain.NodeType;
import com.campusroute.campus_route_backend.dto.RouteStepDto;

// Plain unit test: hand-built nodes, the y axis points down like on the plan
class InstructionServiceTest {

    private final InstructionService service = new InstructionService();

    private static Node node(String id, int floor, int x, int y, NodeType type) {
        return new Node(id, floor, x, y, type, id);
    }

    private List<String> texts(List<RouteStepDto> steps) {
        return steps.stream().map(RouteStepDto::text).toList();
    }

    @Test
    void straightWalkIsMergedAndTurnsAreNamed() {
        // Room 300 px above the corridor (21 m), then 2 x 100 px east (14 m), then up (turn left)
        List<Node> path = List.of(
                node("R", 0, 0, 0, NodeType.ROOM),
                node("C1", 0, 0, 300, NodeType.CORRIDOR),
                node("C2", 0, 100, 300, NodeType.CORRIDOR),
                node("C3", 0, 200, 300, NodeType.CORRIDOR),
                node("D", 0, 200, 100, NodeType.ROOM));

        List<RouteStepDto> steps = service.build(path, null);

        assertThat(texts(steps)).containsExactly(
                "Départ : R",
                "Sortez de la salle R et avancez de 21 m",
                "Tournez à gauche et avancez de 14 m",
                "Tournez à gauche et avancez de 14 m jusqu'à la salle D",
                "Vous êtes arrivé : D");
        assertThat(steps.get(2).icon()).isEqualTo("↰");
    }

    @Test
    void rightTurnAndUTurn() {
        // east, then south = right turn; then north = U-turn
        List<Node> path = List.of(
                node("A", 0, 0, 0, NodeType.CORRIDOR),
                node("B", 0, 100, 0, NodeType.CORRIDOR),
                node("C", 0, 100, 100, NodeType.CORRIDOR),
                node("D", 0, 100, 0, NodeType.CORRIDOR));

        List<RouteStepDto> steps = service.build(path, null);

        assertThat(steps.get(2).text()).isEqualTo("Tournez à droite et avancez de 7 m");
        assertThat(steps.get(2).icon()).isEqualTo("↱");
        assertThat(steps.get(3).text()).startsWith("Faites demi-tour et avancez de 7 m");
        assertThat(steps.get(3).icon()).isEqualTo("↶");
    }

    @Test
    void consecutiveElevatorRidesAreOneStep() {
        List<Node> path = List.of(
                node("C0", 0, 0, 0, NodeType.CORRIDOR),
                node("E0", 0, 100, 0, NodeType.ELEVATOR),
                node("E1", 1, 100, 0, NodeType.ELEVATOR),
                node("E2", 2, 100, 0, NodeType.ELEVATOR),
                node("C2", 2, 0, 0, NodeType.CORRIDOR));

        List<RouteStepDto> steps = service.build(path, null);

        assertThat(texts(steps)).containsExactly(
                "Départ : C0",
                "Avancez de 7 m",
                "Prenez l'ascenseur et montez au 2e étage",
                "Sortez de l'ascenseur et avancez de 7 m jusqu'à : C2",
                "Vous êtes arrivé : C2");
        assertThat(steps.get(2).sub()).isEqualTo("RDC → 2e étage");
    }

    @Test
    void stairsDownward() {
        List<Node> path = List.of(
                node("S2", 2, 0, 0, NodeType.STAIRS),
                node("S1", 1, 0, 0, NodeType.STAIRS));

        List<RouteStepDto> steps = service.build(path, "Salle X");

        assertThat(steps.get(1).text()).isEqualTo("Descendez par l'escalier jusqu'au 1er étage");
        assertThat(steps.get(2).sub()).isEqualTo("Salle X · 1er étage");
    }

    @Test
    void singleNodeGivesDepartureAndArrivalOnly() {
        List<RouteStepDto> steps = service.build(List.of(node("A", 0, 0, 0, NodeType.ROOM)), null);
        assertThat(texts(steps)).containsExactly("Départ : A", "Vous êtes arrivé : A");
    }
}
