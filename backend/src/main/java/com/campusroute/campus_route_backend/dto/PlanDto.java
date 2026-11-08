package com.campusroute.campus_route_backend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Answer of GET /api/plan: everything the front end needs to draw the building. */
@Schema(description = "Toutes les données pour dessiner le bâtiment")
public record PlanDto(
        @Schema(description = "Échelle du plan : mètres par pixel", example = "0.07") double scaleMetersPerPixel,
        @Schema(description = "Étages du bâtiment, triés", example = "[0, 1, 2]") List<Integer> floors,
        @Schema(description = "Toutes les salles, triées par code") List<PlanRoomDto> rooms,
        @Schema(description = "Tous les nœuds du graphe de navigation") List<RouteNodeDto> nodes,
        @Schema(description = "Tous les liens entre nœuds") List<PlanEdgeDto> edges) { }
