package com.campusroute.campus_route_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.campusroute.campus_route_backend.domain.Edge;
import com.campusroute.campus_route_backend.domain.EdgeKind;
import com.campusroute.campus_route_backend.domain.Node;
import com.campusroute.campus_route_backend.domain.NodeType;
import com.campusroute.campus_route_backend.dto.RouteStepDto;

// Plain unit test: hand-built nodes, the y axis points down like on the plan
class InstructionServiceTest {

    private final InstructionService service = new InstructionService();

    // A node of building A
    private static Node node(String id, int floor, int x, int y, NodeType type) {
        return new Node(id, "A", floor, x, y, type, id);
    }

    private static Node node(String id, String building, int x, int y, NodeType type, String label) {
        return new Node(id, building, 0, x, y, type, label);
    }

    // Edges between consecutive nodes: WALK on a floor, elevator or stairs (from the node type) between floors
    private static List<Edge> edgesOf(List<Node> path) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 1; i < path.size(); i++) {
            Node a = path.get(i - 1);
            Node b = path.get(i);
            EdgeKind kind = a.getFloor() == b.getFloor() ? EdgeKind.WALK
                    : a.getType() == NodeType.ELEVATOR ? EdgeKind.ELEVATOR : EdgeKind.STAIRS;
            edges.add(new Edge(a, b, 1, kind));
        }
        return edges;
    }

    private List<RouteStepDto> build(List<Node> path, String destinationName) {
        return service.build(path, edgesOf(path), destinationName);
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

        List<RouteStepDto> steps = build(path, null);

        assertThat(texts(steps)).containsExactly(
                "Départ : R",
                "Sortez de la salle R et avancez de 21 m",
                "Tournez à gauche et avancez de 14 m",
                "Tournez à gauche et avancez de 14 m jusqu'à la salle D",
                "Vous êtes arrivé : D");
        assertThat(steps.get(2).icon()).isEqualTo("↰");
        assertThat(steps).allSatisfy(s -> assertThat(s.building()).isEqualTo("A"));
    }

    @Test
    void rightTurnAndUTurn() {
        // east, then south = right turn; then north = U-turn
        List<Node> path = List.of(
                node("A", 0, 0, 0, NodeType.CORRIDOR),
                node("B", 0, 100, 0, NodeType.CORRIDOR),
                node("C", 0, 100, 100, NodeType.CORRIDOR),
                node("D", 0, 100, 0, NodeType.CORRIDOR));

        List<RouteStepDto> steps = build(path, null);

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

        List<RouteStepDto> steps = build(path, null);

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

        List<RouteStepDto> steps = build(path, "Salle X");

        assertThat(steps.get(1).text()).isEqualTo("Descendez par l'escalier jusqu'au 1er étage");
        assertThat(steps.get(2).sub()).isEqualTo("Salle X · 1er étage");
    }

    @Test
    void leavingABuildingCrossingTheCourtyardAndEnteringAnother() {
        // Each plan has its own coordinates: A (0,0)..., EXT (500,500)..., B (300,0)...
        List<Node> path = List.of(
                node("R", "A", 0, 0, NodeType.ROOM, "R"),
                node("AC", "A", 0, 100, NodeType.CORRIDOR, "Couloir"),
                node("AD", "A", 0, 200, NodeType.ENTRANCE, "Bâtiment A – porte cour"),
                node("P1", "EXT", 500, 500, NodeType.OUTDOOR, "Cour"),
                node("P2", "EXT", 500, 643, NodeType.OUTDOOR, "Parvis"),       // 143 px = 10 m south
                node("BD", "B", 300, 0, NodeType.ENTRANCE, "Bâtiment B – porte cour"),
                node("BC", "B", 300, 100, NodeType.CORRIDOR, "Couloir"),        // 7 m south
                node("X", "B", 400, 100, NodeType.ROOM, "X"));                  // then east = left turn

        List<RouteStepDto> steps = build(path, "Salle X");

        assertThat(texts(steps)).containsExactly(
                "Départ : R",
                "Sortez de la salle R et avancez de 14 m",
                "Sortez du bâtiment A",
                "Avancez de 10 m",
                "Entrez dans le bâtiment B",
                "Avancez de 7 m",
                "Tournez à gauche et avancez de 7 m jusqu'à la salle X",
                "Vous êtes arrivé : X");
        assertThat(steps.get(2).sub()).isEqualTo("Bâtiment A – porte cour");
        assertThat(steps.get(2).icon()).isEqualTo("⇄");
        assertThat(steps.get(3).sub()).isEqualTo("Extérieur");
        assertThat(steps.get(3).building()).isEqualTo("EXT");
        assertThat(steps.get(4).sub()).isEqualTo("Bâtiment B – porte cour");
        assertThat(steps.get(4).building()).isEqualTo("B");
        assertThat(steps.get(7).sub()).isEqualTo("Salle X · RDC");
    }

    @Test
    void outdoorStepsFromTheCampusEntrance() {
        Node entrance = node("ENTREE", "EXT", 0, 0, NodeType.ENTRANCE, "Entrée du campus");
        Node bottom = node("P4", "EXT", 0, 100, NodeType.OUTDOOR, "Bas des marches");
        Node top = node("P5", "EXT", 0, 200, NodeType.OUTDOOR, "Haut des marches");
        Node square = node("P6", "EXT", 0, 300, NodeType.OUTDOOR, "Parvis");
        List<Node> path = List.of(entrance, bottom, top, square);
        List<Edge> edges = List.of(
                new Edge(entrance, bottom, 100, EdgeKind.WALK),
                new Edge(bottom, top, 100, EdgeKind.STAIRS), // same floor: a flight of steps
                new Edge(top, square, 100, EdgeKind.WALK));

        List<RouteStepDto> steps = service.build(path, edges, null);

        assertThat(texts(steps)).containsExactly(
                "Départ : Entrée du campus",
                "Depuis l'entrée du campus, avancez de 7 m",
                "Empruntez l'escalier extérieur",
                "Avancez de 7 m jusqu'à : Parvis",
                "Vous êtes arrivé : Parvis");
        assertThat(steps.get(0).sub()).isEqualTo("Extérieur");
        assertThat(steps.get(2).icon()).isEqualTo("↕");
    }

    @Test
    void singleNodeGivesDepartureAndArrivalOnly() {
        List<RouteStepDto> steps = build(List.of(node("A", 0, 0, 0, NodeType.ROOM)), null);
        assertThat(texts(steps)).containsExactly("Départ : A", "Vous êtes arrivé : A");
    }

    @Test
    void edgesMustMatchThePath() {
        List<Node> path = List.of(node("A", 0, 0, 0, NodeType.ROOM), node("B", 0, 100, 0, NodeType.ROOM));
        assertThatThrownBy(() -> service.build(path, List.of(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
