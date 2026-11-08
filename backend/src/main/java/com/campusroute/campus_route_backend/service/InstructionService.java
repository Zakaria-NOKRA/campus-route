package com.campusroute.campus_route_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.campusroute.campus_route_backend.domain.Node;
import com.campusroute.campus_route_backend.domain.NodeType;
import com.campusroute.campus_route_backend.dto.RouteStepDto;

/** Turns a list of nodes into GPS-like instructions in French. No database access. */
@Service
public class InstructionService {

    // Two directions with a dot product above this are "the same direction" (straight on)
    private static final double STRAIGHT_DOT = 0.99;
    // A cross product above this (in absolute value) is a real turn
    private static final double TURN_CROSS = 0.3;

    private enum Turn { LEFT, RIGHT, U_TURN }

    // A group of consecutive path nodes: either a walk in one direction or a floor change
    private static class Segment {
        boolean floorChange;
        NodeType via;      // floor change: STAIRS or ELEVATOR
        int fromFloor;
        int toFloor;
        int floor;         // floor where the segment starts
        double lengthPx;   // walk: length in pixels
        Turn turn;         // walk: turn made to enter this segment (null = none)
    }

    /**
     * @param path            ordered nodes of the route (at least one)
     * @param destinationName name of the destination room, for the last step (may be null)
     */
    public List<RouteStepDto> build(List<Node> path, String destinationName) {
        List<Segment> segments = groupIntoSegments(path);

        Node start = path.get(0);
        Node dest = path.get(path.size() - 1);
        List<RouteStepDto> steps = new ArrayList<>();
        steps.add(new RouteStepDto("Départ : " + start.getLabel(), floorName(start.getFloor()),
                start.getFloor(), "●"));

        for (int i = 0; i < segments.size(); i++) {
            Segment s = segments.get(i);
            if (s.floorChange) {
                steps.add(floorChangeStep(s));
            } else {
                Segment before = i > 0 ? segments.get(i - 1) : null;
                boolean isLast = i == segments.size() - 1;
                steps.add(walkStep(s, before, i == 0, isLast, start, dest));
            }
        }

        String sub = destinationName != null
                ? destinationName + " · " + floorName(dest.getFloor())
                : floorName(dest.getFloor());
        steps.add(new RouteStepDto("Vous êtes arrivé : " + dest.getLabel(), sub, dest.getFloor(), "⚑"));
        return steps;
    }

    private List<Segment> groupIntoSegments(List<Node> path) {
        List<Segment> segments = new ArrayList<>();
        double[] previousDir = null;
        for (int i = 1; i < path.size(); i++) {
            Node a = path.get(i - 1);
            Node b = path.get(i);
            Segment last = segments.isEmpty() ? null : segments.get(segments.size() - 1);

            if (a.getFloor() != b.getFloor()) {
                // Consecutive floor changes by the same means (E0>E1>E2) make one step
                if (last != null && last.floorChange && last.via == a.getType()) {
                    last.toFloor = b.getFloor();
                } else {
                    Segment s = new Segment();
                    s.floorChange = true;
                    s.via = a.getType();
                    s.fromFloor = a.getFloor();
                    s.toFloor = b.getFloor();
                    s.floor = a.getFloor();
                    segments.add(s);
                }
                previousDir = null;
                continue;
            }

            double dx = b.getX() - a.getX();
            double dy = b.getY() - a.getY();
            double length = Math.hypot(dx, dy);
            double[] dir = {dx / length, dy / length};

            if (last != null && !last.floorChange && previousDir != null && dot(previousDir, dir) > STRAIGHT_DOT) {
                last.lengthPx += length; // straight on: extend the current walk
            } else {
                Segment s = new Segment();
                s.lengthPx = length;
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
        String text = s.via == NodeType.ELEVATOR
                ? "Prenez l'ascenseur et " + verb + " au " + floorName(s.toFloor)
                : capitalize(verb) + " par l'escalier jusqu'au " + floorName(s.toFloor);
        return new RouteStepDto(text, floorName(s.fromFloor) + " → " + floorName(s.toFloor), s.floor, "↕");
    }

    private RouteStepDto walkStep(Segment s, Segment before, boolean isFirst, boolean isLast,
                                  Node start, Node dest) {
        long meters = Math.max(1, Math.round(s.lengthPx * RouteConstants.METERS_PER_PIXEL));
        String text;
        if (s.turn == Turn.U_TURN) {
            text = "Faites demi-tour et avancez de " + meters + " m";
        } else if (s.turn != null) {
            text = "Tournez à " + (s.turn == Turn.RIGHT ? "droite" : "gauche") + " et avancez de " + meters + " m";
        } else if (before != null && before.floorChange) {
            text = "Sortez " + (before.via == NodeType.ELEVATOR ? "de l'ascenseur" : "de l'escalier")
                    + " et avancez de " + meters + " m";
        } else if (isFirst && start.getType() == NodeType.ROOM) {
            text = "Sortez de la salle " + start.getLabel() + " et avancez de " + meters + " m";
        } else if (isFirst && start.getType() == NodeType.ENTRANCE) {
            text = "Depuis l'entrée, avancez de " + meters + " m jusqu'au couloir";
        } else {
            text = "Avancez de " + meters + " m";
        }
        if (isLast) {
            text += dest.getType() == NodeType.ROOM
                    ? " jusqu'à la salle " + dest.getLabel()
                    : " jusqu'à : " + dest.getLabel();
        }
        String icon = s.turn == Turn.RIGHT ? "↱" : s.turn == Turn.LEFT ? "↰" : s.turn == Turn.U_TURN ? "↶" : "↑";
        return new RouteStepDto(text, floorName(s.floor), s.floor, icon);
    }

    private String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    static String floorName(int floor) {
        return floor == 0 ? "RDC" : floor == 1 ? "1er étage" : floor + "e étage";
    }
}
