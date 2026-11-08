package com.campusroute.campus_route_backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import com.campusroute.campus_route_backend.domain.Edge;
import com.campusroute.campus_route_backend.domain.EdgeKind;
import com.campusroute.campus_route_backend.domain.Node;

/** Dijkstra on the navigation graph. It does not read the database: the graph is a parameter. */
@Service
public class PathfinderService {

    /** The nodes of the path in order, and the edges walked between them (one less than nodes). */
    public record PathResult(List<Node> nodes, List<Edge> edges) { }

    // An edge seen from one of its ends
    private record Link(String to, Edge edge) { }
    // How we reached a node: from which node, through which edge
    private record Arrival(String from, Edge edge) { }
    // Entry of the priority queue: a node and the cost to reach it
    private record Candidate(String id, int cost) { }

    /**
     * Cheapest path from one node to another; edges are undirected and the cost is their weight.
     * With accessible = true the STAIRS edges are ignored.
     */
    public PathResult shortestPath(Collection<Node> nodes, Collection<Edge> edges,
                                   String from, String to, boolean accessible) {
        Map<String, Node> nodesById = new HashMap<>();
        for (Node n : nodes) {
            nodesById.put(n.getId(), n);
        }
        if (!nodesById.containsKey(from)) {
            throw new NodeNotFoundException(from);
        }
        if (!nodesById.containsKey(to)) {
            throw new NodeNotFoundException(to);
        }

        // Adjacency list: each edge is added in both directions
        Map<String, List<Link>> neighbours = new HashMap<>();
        for (Edge e : edges) {
            if (accessible && e.getKind() == EdgeKind.STAIRS) {
                continue;
            }
            String a = e.getA().getId();
            String b = e.getB().getId();
            neighbours.computeIfAbsent(a, k -> new ArrayList<>()).add(new Link(b, e));
            neighbours.computeIfAbsent(b, k -> new ArrayList<>()).add(new Link(a, e));
        }

        Map<String, Integer> cost = new HashMap<>();
        Map<String, Arrival> arrivals = new HashMap<>();
        PriorityQueue<Candidate> queue = new PriorityQueue<>((x, y) -> Integer.compare(x.cost(), y.cost()));
        cost.put(from, 0);
        queue.add(new Candidate(from, 0));

        while (!queue.isEmpty()) {
            Candidate current = queue.poll();
            if (current.cost() > cost.get(current.id())) {
                continue; // outdated entry: a cheaper way to this node was found since
            }
            if (current.id().equals(to)) {
                break; // the cheapest cost to the destination is final
            }
            for (Link link : neighbours.getOrDefault(current.id(), List.of())) {
                int newCost = current.cost() + link.edge().getWeight();
                if (newCost < cost.getOrDefault(link.to(), Integer.MAX_VALUE)) {
                    cost.put(link.to(), newCost);
                    arrivals.put(link.to(), new Arrival(current.id(), link.edge()));
                    queue.add(new Candidate(link.to(), newCost));
                }
            }
        }

        if (!cost.containsKey(to)) {
            throw new NoRouteException(from, to, accessible);
        }

        // Walk back from the destination to the start, then reverse
        List<Node> pathNodes = new ArrayList<>();
        List<Edge> pathEdges = new ArrayList<>();
        String step = to;
        pathNodes.add(nodesById.get(step));
        while (!step.equals(from)) {
            Arrival arrival = arrivals.get(step);
            pathEdges.add(arrival.edge());
            step = arrival.from();
            pathNodes.add(nodesById.get(step));
        }
        Collections.reverse(pathNodes);
        Collections.reverse(pathEdges);
        return new PathResult(pathNodes, pathEdges);
    }
}
