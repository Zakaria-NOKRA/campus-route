package com.campusroute.campus_route_backend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Answer of GET /api/route. */
@Schema(description = "Itinéraire entre deux points")
public record RouteDto(
        @Schema(description = "Distance à pied en mètres (les escaliers et l'ascenseur ne comptent pas)", example = "54.6") double distanceMeters,
        @Schema(description = "Durée estimée en secondes (marche + escaliers/ascenseur)", example = "62.5") double durationSeconds,
        @Schema(description = "Nœuds du chemin, du départ à l'arrivée, pour tracer le trajet sur le plan") List<RouteNodeDto> path,
        @Schema(description = "Instructions pas à pas, en français") List<RouteStepDto> steps) { }
