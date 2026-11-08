package com.campusroute.campus_route_backend.service;

/** Cost model of the route calculation, same values as the prototype. */
final class RouteConstants {

    /** One plan pixel is 0.07 m. */
    static final double METERS_PER_PIXEL = 0.07;

    /** Average walking speed, in metres per second. */
    static final double WALKING_SPEED_MPS = 1.3;

    /** Extra time for one stairs edge (its length is not counted as walking). */
    static final int STAIRS_SECONDS = 12;

    /** Extra time for one elevator edge (waiting + ride). */
    static final int ELEVATOR_SECONDS = 25;

    private RouteConstants() { }
}
