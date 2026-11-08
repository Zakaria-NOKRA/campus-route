package com.campusroute.campus_route_backend.dto;

import com.campusroute.campus_route_backend.domain.Node;

import io.swagger.v3.oas.annotations.media.Schema;

/** A point of the route, with what the front end needs to draw it on the plan. */
@Schema(description = "Point du graphe de navigation (salle, couloir, escalier, ascenseur, entrée)")
public record RouteNodeDto(
        @Schema(description = "Identifiant du nœud ; pour une salle, c'est son code", example = "B204") String id,
        @Schema(description = "Étage du nœud", example = "2") int floor,
        @Schema(description = "Position horizontale sur le plan de l'étage, en pixels (1 px = 0,07 m)", example = "536") int x,
        @Schema(description = "Position verticale sur le plan de l'étage, en pixels", example = "115") int y,
        @Schema(description = "Type de nœud", allowableValues = {"CORRIDOR", "STAIRS", "ELEVATOR", "ROOM", "ENTRANCE"}, example = "ROOM") String type,
        @Schema(description = "Libellé affichable du nœud", example = "B204") String label) {

    public static RouteNodeDto from(Node node) {
        return new RouteNodeDto(node.getId(), node.getFloor(), node.getX(), node.getY(),
                node.getType().name(), node.getLabel());
    }
}
