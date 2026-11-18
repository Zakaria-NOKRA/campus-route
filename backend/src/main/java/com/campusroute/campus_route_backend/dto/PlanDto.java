package com.campusroute.campus_route_backend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Answer of GET /api/plan?building=: everything the front end needs to draw one building. */
@Schema(description = "Toutes les données pour dessiner un bâtiment (ou l'extérieur)")
public record PlanDto(
        @Schema(description = "Code du bâtiment ; EXT pour l'extérieur", example = "A") String building,
        @Schema(description = "Nom affichable du bâtiment", example = "Bâtiment A – Sciences") String name,
        @Schema(description = "Échelle du plan : mètres par pixel", example = "0.07") double scaleMetersPerPixel,
        @Schema(description = "Largeur du plan, en pixels", example = "1000") int width,
        @Schema(description = "Hauteur du plan, en pixels", example = "800") int height,
        @Schema(description = "Contour du bâtiment (attribut SVG points)", example = "0,0 1000,0 1000,800 700,800 700,300 0,300") String outline,
        @Schema(description = "Étages du bâtiment, triés", example = "[0, 1, 2]") List<Integer> floors,
        @Schema(description = "Salles du bâtiment, triées par code") List<PlanRoomDto> rooms,
        @Schema(description = "Nœuds du graphe de navigation dans ce bâtiment") List<RouteNodeDto> nodes,
        @Schema(description = "Liens entre deux nœuds de ce bâtiment") List<PlanEdgeDto> edges) { }
