package com.campusroute.campus_route_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.campusroute.campus_route_backend.domain.Edge;
import com.campusroute.campus_route_backend.domain.EdgeKind;
import com.campusroute.campus_route_backend.domain.Node;
import com.campusroute.campus_route_backend.domain.NodeType;

// Plain unit test: no Spring, no database, a tiny graph built by hand
class PathfinderServiceTest {

    private final PathfinderService pathfinder = new PathfinderService();

    private static Node node(String id, int floor) {
        return new Node(id, floor, 0, 0, NodeType.CORRIDOR, id);
    }

    private final Node a = node("A", 0);
    private final Node b = node("B", 0);
    private final Node c = node("C", 1);
    private final Node lonely = node("LONELY", 0);

    // A -10- B, then B -50- C by stairs and B -200- C by elevator
    private final List<Node> nodes = List.of(a, b, c, lonely);
    private final List<Edge> edges = List.of(
            new Edge(a, b, 10, EdgeKind.WALK),
            new Edge(b, c, 50, EdgeKind.STAIRS),
            new Edge(b, c, 200, EdgeKind.ELEVATOR));

    private List<String> ids(PathfinderService.PathResult r) {
        return r.nodes().stream().map(Node::getId).toList();
    }

    @Test
    void edgesAreUndirected() {
        assertThat(ids(pathfinder.shortestPath(nodes, edges, "B", "A", false))).containsExactly("B", "A");
        assertThat(ids(pathfinder.shortestPath(nodes, edges, "A", "B", false))).containsExactly("A", "B");
    }

    @Test
    void picksTheCheapestEdge() {
        var result = pathfinder.shortestPath(nodes, edges, "A", "C", false);
        assertThat(result.edges()).extracting(Edge::getKind).containsExactly(EdgeKind.WALK, EdgeKind.STAIRS);
    }

    @Test
    void accessibleIgnoresStairsEvenIfCheaper() {
        var result = pathfinder.shortestPath(nodes, edges, "A", "C", true);
        assertThat(result.edges()).extracting(Edge::getKind).containsExactly(EdgeKind.WALK, EdgeKind.ELEVATOR);
    }

    @Test
    void sameNodeGivesASingleNodePath() {
        var result = pathfinder.shortestPath(nodes, edges, "A", "A", false);
        assertThat(ids(result)).containsExactly("A");
        assertThat(result.edges()).isEmpty();
    }

    @Test
    void disconnectedNodeThrowsNoRoute() {
        assertThatThrownBy(() -> pathfinder.shortestPath(nodes, edges, "A", "LONELY", false))
                .isInstanceOf(NoRouteException.class)
                .hasMessage("Aucun itinéraire entre A et LONELY");
    }

    @Test
    void stairsOnlyLinkMakesAccessibleRouteImpossible() {
        List<Edge> stairsOnly = List.of(new Edge(a, b, 10, EdgeKind.WALK), new Edge(b, c, 50, EdgeKind.STAIRS));
        assertThatThrownBy(() -> pathfinder.shortestPath(nodes, stairsOnly, "A", "C", true))
                .isInstanceOf(NoRouteException.class)
                .hasMessage("Aucun itinéraire entre A et C sans escalier");
    }

    @Test
    void unknownNodeThrows() {
        assertThatThrownBy(() -> pathfinder.shortestPath(nodes, edges, "A", "X", false))
                .isInstanceOf(NodeNotFoundException.class);
        assertThatThrownBy(() -> pathfinder.shortestPath(nodes, edges, "X", "A", false))
                .isInstanceOf(NodeNotFoundException.class);
    }
}
