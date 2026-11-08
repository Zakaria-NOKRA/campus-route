package com.campusroute.campus_route_backend.dto;

import com.campusroute.campus_route_backend.domain.Room;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Salle avec ce qu'il faut pour la dessiner sur le plan")
public record PlanRoomDto(
        @Schema(description = "Code de la salle", example = "B204") String code,
        @Schema(description = "Nom complet de la salle", example = "Salle de TD B204") String name,
        @Schema(description = "Type de salle", example = "Salle de TD") String type,
        @Schema(description = "Étiquette courte affichée sur le plan", example = "TD") String tag,
        @Schema(description = "Étage (0 = rez-de-chaussée)", example = "2") int floor,
        @Schema(description = "Colonne de la salle dans la grille du bâtiment", example = "3") int slot,
        @Schema(description = "true si la salle est sur la rangée du haut, false pour la rangée du bas", example = "true") boolean top,
        @Schema(description = "Position horizontale de la porte (nœud de la salle), en pixels", example = "536") int x,
        @Schema(description = "Position verticale de la porte (nœud de la salle), en pixels", example = "115") int y) {

    public static PlanRoomDto from(Room room) {
        return new PlanRoomDto(room.getCode(), room.getName(), room.getType(), room.getTag(),
                room.getFloor(), room.getSlot(), room.isTop(),
                room.getNode().getX(), room.getNode().getY());
    }
}
