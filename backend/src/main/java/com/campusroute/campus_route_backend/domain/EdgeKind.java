package com.campusroute.campus_route_backend.domain;

/** How an edge is travelled. STAIRS edges are excluded from accessible routes. */
public enum EdgeKind {
    WALK, STAIRS, ELEVATOR
}
