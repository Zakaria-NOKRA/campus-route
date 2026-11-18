package com.campusroute.campus_route_backend.dto;

import com.campusroute.campus_route_backend.domain.Room;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Salle avec ce qu'il faut pour la dessiner sur le plan")
public record PlanRoomDto(
        @Schema(description = "Code de la salle", example = "A204") String code,
        @Schema(description = "Nom complet de la salle", example = "Salle de TD A204") String name,
        @Schema(description = "Type de salle", example = "Salle de TD") String type,
        @Schema(description = "Étiquette courte affichée sur le plan", example = "TD") String tag,
        @Schema(description = "Étage (0 = rez-de-chaussée)", example = "2") int floor,
        @Schema(description = "Code du bâtiment", example = "A") String building,
        @Schema(description = "Rectangle de la salle : bord gauche, en pixels", example = "750") int x,
        @Schema(description = "Rectangle de la salle : bord haut, en pixels", example = "20") int y,
        @Schema(description = "Largeur du rectangle, en pixels", example = "230") int width,
        @Schema(description = "Hauteur du rectangle, en pixels", example = "100") int height,
        @Schema(description = "Position horizontale de la porte (nœud de la salle), en pixels", example = "865") int doorX,
        @Schema(description = "Position verticale de la porte (nœud de la salle), en pixels", example = "120") int doorY) {

    public static PlanRoomDto from(Room room) {
        return new PlanRoomDto(room.getCode(), room.getName(), room.getType(), room.getTag(),
                room.getFloor(), room.getBuilding(),
                room.getX(), room.getY(), room.getWidth(), room.getHeight(),
                room.getNode().getX(), room.getNode().getY());
    }
}
