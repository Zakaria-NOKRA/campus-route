package com.campusroute.campus_route_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.campusroute.campus_route_backend.domain.Edge;
import com.campusroute.campus_route_backend.domain.EdgeKind;
import com.campusroute.campus_route_backend.domain.Node;
import com.campusroute.campus_route_backend.domain.NodeType;
import com.campusroute.campus_route_backend.dto.RouteStepDto;

/** Turns a path (nodes + edges) into GPS-like instructions in French. No database access. */
@Service
public class InstructionService {

    /** Code of the outdoor plan space (courtyard, paths). */
    static final String OUTDOOR = "EXT";

    // Two directions with a dot product above this are "the same direction" (straight on)
    private static final double STRAIGHT_DOT = 0.99;
    // A cross product above this (in absolute value) is a real turn
    private static final double TURN_CROSS = 0.3;

    private enum Turn { LEFT, RIGHT, U_TURN }

    // WALK: a walk in one direction. FLOOR_CHANGE: stairs or elevator to another floor.
    // OUTDOOR_STEPS: a flight of steps without changing floor. LEAVE / ENTER: through a building door.
    private enum Kind { WALK, FLOOR_CHANGE, OUTDOOR_STEPS, LEAVE, ENTER }

    // A group of consecutive path nodes that makes one instruction
    private static class Segment {
        Kind kind;
        String building;   // plan space where the segment happens
        int floor;         // floor where the segment starts
        EdgeKind via;      // floor change: STAIRS or ELEVATOR
        int fromFloor;
        int toFloor;
        Node door;         // LEAVE / ENTER: the building door node
        double lengthPx;   // walk: length in pixels
        Turn turn;         // walk: turn made to enter this segment (null = none)
    }

    /**
     * @param path            ordered nodes of the route (at least one)
     * @param edges           edges walked between them: edges.get(i) links path.get(i) and path.get(i + 1)
     * @param destinationName name of the destination room, for the last step (may be null)
     */
    public List<RouteStepDto> build(List<Node> path, List<Edge> edges, String destinationName) {
        if (edges.size() != path.size() - 1) {
            throw new IllegalArgumentException("A path of n nodes needs n - 1 edges");
        }
        List<Segment> segments = groupIntoSegments(path, edges);

        Node start = path.get(0);
        Node dest = path.get(path.size() - 1);
        List<RouteStepDto> steps = new ArrayList<>();
        steps.add(new RouteStepDto("Départ : " + start.getLabel(), placeName(start.getBuilding(), start.getFloor()),
                start.getBuilding(), start.getFloor(), "●"));

        for (int i = 0; i < segments.size(); i++) {
            Segment s = segments.get(i);
            Segment before = i > 0 ? segments.get(i - 1) : null;
            boolean isLast = i == segments.size() - 1;
            steps.add(switch (s.kind) {
                case FLOOR_CHANGE -> floorChangeStep(s);
                case OUTDOOR_STEPS -> new RouteStepDto("Empruntez l'escalier extérieur",
                        placeName(s.building, s.floor), s.building, s.floor, "↕");
                case LEAVE -> new RouteStepDto("Sortez du bâtiment " + s.building, s.door.getLabel(),
                        s.building, s.floor, "⇄");
                case ENTER -> new RouteStepDto("Entrez dans le bâtiment " + s.building, s.door.getLabel(),
                        s.building, s.floor, "⇄");
                case WALK -> walkStep(s, before, i == 0, isLast, start, dest);
            });
        }

        String place = placeName(dest.getBuilding(), dest.getFloor());
        String sub = destinationName != null ? destinationName + " · " + place : place;
        steps.add(new RouteStepDto("Vous êtes arrivé : " + dest.getLabel(), sub,
                dest.getBuilding(), dest.getFloor(), "⚑"));
        return steps;
    }

    private List<Segment> groupIntoSegments(List<Node> path, List<Edge> edges) {
        List<Segment> segments = new ArrayList<>();
        double[] previousDir = null;
        for (int i = 1; i < path.size(); i++) {
            Node a = path.get(i - 1);
            Node b = path.get(i);
            EdgeKind edgeKind = edges.get(i - 1).getKind();
            Segment last = segments.isEmpty() ? null : segments.get(segments.size() - 1);

            // 1. Through a building door: the two nodes are on two different plans,
            //    so their coordinates cannot be compared and the direction starts again
            if (!a.getBuilding().equals(b.getBuilding())) {
                Segment s = new Segment();
                boolean leaving = b.getBuilding().equals(OUTDOOR);
                s.kind = leaving ? Kind.LEAVE : Kind.ENTER;
                s.door = leaving ? a : b;
                s.building = s.door.getBuilding();
                s.floor = s.door.getFloor();
                segments.add(s);
                previousDir = null;
                continue;
            }

            // 2. Change of floor; consecutive rides by the same means (E0>E1>E2) make one step
            if (a.getFloor() != b.getFloor()) {
                if (last != null && last.kind == Kind.FLOOR_CHANGE && last.via == edgeKind) {
                    last.toFloor = b.getFloor();
                } else {
                    Segment s = new Segment();
                    s.kind = Kind.FLOOR_CHANGE;
                    s.via = edgeKind;
                    s.building = a.getBuilding();
                    s.fromFloor = a.getFloor();
                    s.toFloor = b.getFloor();
                    s.floor = a.getFloor();
                    segments.add(s);
                }
                previousDir = null;
                continue;
            }

            // 3. A flight of steps on the same floor (outdoor steps)
            if (edgeKind == EdgeKind.STAIRS) {
                Segment s = new Segment();
                s.kind = Kind.OUTDOOR_STEPS;
                s.building = a.getBuilding();
                s.floor = a.getFloor();
                segments.add(s);
                previousDir = null;
                continue;
            }

            // 4. A walk: extended while it goes straight on, a new segment at each turn
            double dx = b.getX() - a.getX();
            double dy = b.getY() - a.getY();
            double length = Math.hypot(dx, dy);
            double[] dir = {dx / length, dy / length};

            if (last != null && last.kind == Kind.WALK && previousDir != null && dot(previousDir, dir) > STRAIGHT_DOT) {
                last.lengthPx += length; // straight on: extend the current walk
            } else {
                Segment s = new Segment();
                s.kind = Kind.WALK;
                s.lengthPx = length;
                s.building = a.getBuilding();
                s.floor = a.getFloor();
                s.turn = previousDir == null ? null : turnBetween(previousDir, dir);
                segments.add(s);
            }
            previousDir = dir;
        }
        return segments;
    }

    private Turn turnBetween(double[] previous, double[] current) {
        // The y axis points down on the plan, so a positive cross product is a right turn
        double cross = previous[0] * current[1] - previous[1] * current[0];
        if (Math.abs(cross) > TURN_CROSS) {
            return cross > 0 ? Turn.RIGHT : Turn.LEFT;
        }
        return dot(previous, current) < 0 ? Turn.U_TURN : null;
    }

    private double dot(double[] u, double[] v) {
        return u[0] * v[0] + u[1] * v[1];
    }

    private RouteStepDto floorChangeStep(Segment s) {
        String verb = s.toFloor > s.fromFloor ? "montez" : "descendez";
        String text = s.via == EdgeKind.ELEVATOR
                ? "Prenez l'ascenseur et " + verb + " au " + floorName(s.toFloor)
                : capitalize(verb) + " par l'escalier jusqu'au " + floorName(s.toFloor);
        return new RouteStepDto(text, floorName(s.fromFloor) + " → " + floorName(s.toFloor),
                s.building, s.floor, "↕");
    }

    private RouteStepDto walkStep(Segment s, Segment before, boolean isFirst, boolean isLast,
                                  Node start, Node dest) {
        long meters = Math.max(1, Math.round(s.lengthPx * RouteConstants.METERS_PER_PIXEL));
        String text;
        if (s.turn == Turn.U_TURN) {
            text = "Faites demi-tour et avancez de " + meters + " m";
        } else if (s.turn != null) {
            text = "Tournez à " + (s.turn == Turn.RIGHT ? "droite" : "gauche") + " et avancez de " + meters + " m";
        } else if (before != null && before.kind == Kind.FLOOR_CHANGE) {
            text = "Sortez " + (before.via == EdgeKind.ELEVATOR ? "de l'ascenseur" : "de l'escalier")
                    + " et avancez de " + meters + " m";
        } else if (isFirst && start.getType() == NodeType.ROOM) {
            text = "Sortez de la salle " + start.getLabel() + " et avancez de " + meters + " m";
        } else if (isFirst && start.getType() == NodeType.ENTRANCE) {
            text = (OUTDOOR.equals(start.getBuilding()) ? "Depuis l'entrée du campus" : "Depuis l'entrée")
                    + ", avancez de " + meters + " m";
        } else {
            text = "Avancez de " + meters + " m";
        }
        if (isLast) {
            text += dest.getType() == NodeType.ROOM
                    ? " jusqu'à la salle " + dest.getLabel()
                    : " jusqu'à : " + dest.getLabel();
        }
        String icon = s.turn == Turn.RIGHT ? "↱" : s.turn == Turn.LEFT ? "↰" : s.turn == Turn.U_TURN ? "↶" : "↑";
        return new RouteStepDto(text, placeName(s.building, s.floor), s.building, s.floor, icon);
    }

    private String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    // "Extérieur" outdoors, else the floor name ("RDC", "1er étage"...)
    static String placeName(String building, int floor) {
        return OUTDOOR.equals(building) ? "Extérieur" : floorName(floor);
    }

    static String floorName(int floor) {
        return floor == 0 ? "RDC" : floor == 1 ? "1er étage" : floor + "e étage";
    }
}
