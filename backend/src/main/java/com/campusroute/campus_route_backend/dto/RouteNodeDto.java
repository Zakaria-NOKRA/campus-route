package com.campusroute.campus_route_backend.dto;

import com.campusroute.campus_route_backend.domain.Node;

import io.swagger.v3.oas.annotations.media.Schema;

/** A point of the route, with what the front end needs to draw it on the plan. */
@Schema(description = "Point du graphe de navigation (salle, couloir, escalier, ascenseur, entrée, extérieur)")
public record RouteNodeDto(
        @Schema(description = "Identifiant du nœud ; pour une salle, c'est son code", example = "A204") String id,
        @Schema(description = "Plan du nœud : code du bâtiment, ou EXT pour l'extérieur", example = "A") String building,
        @Schema(description = "Étage du nœud", example = "2") int floor,
        @Schema(description = "Position horizontale sur le plan de son bâtiment et de son étage, en pixels (1 px = 0,07 m)", example = "865") int x,
        @Schema(description = "Position verticale sur le plan de son bâtiment et de son étage, en pixels", example = "120") int y,
        @Schema(description = "Type de nœud", allowableValues = {"CORRIDOR", "STAIRS", "ELEVATOR", "ROOM", "ENTRANCE", "OUTDOOR"}, example = "ROOM") String type,
        @Schema(description = "Libellé affichable du nœud", example = "A204") String label) {

    public static RouteNodeDto from(Node node) {
        return new RouteNodeDto(node.getId(), node.getBuilding(), node.getFloor(), node.getX(), node.getY(),
                node.getType().name(), node.getLabel());
    }
}
