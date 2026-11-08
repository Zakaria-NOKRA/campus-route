package com.campusroute.campus_route_backend.dto;

import com.campusroute.campus_route_backend.domain.Node;

/** A point of the route, with what the front end needs to draw it on the plan. */
public record RouteNodeDto(String id, int floor, int x, int y, String type, String label) {

    public static RouteNodeDto from(Node node) {
        return new RouteNodeDto(node.getId(), node.getFloor(), node.getX(), node.getY(),
                node.getType().name(), node.getLabel());
    }
}
