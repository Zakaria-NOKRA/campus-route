package com.campusroute.campus_route_backend.dto;

/** One instruction of the route, in French. "sub" is a short complement (floor, room name...). */
public record RouteStepDto(String text, String sub, int floor, String icon) { }
