package com.campusroute.campus_route_backend.dto;

import java.util.List;

/** Answer of GET /api/route. */
public record RouteDto(double distanceMeters, double durationSeconds,
                       List<RouteNodeDto> path, List<RouteStepDto> steps) { }
