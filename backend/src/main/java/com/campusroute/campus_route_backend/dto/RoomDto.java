package com.campusroute.campus_route_backend.dto;

import java.util.List;

import com.campusroute.campus_route_backend.domain.Room;

import io.swagger.v3.oas.annotations.media.Schema;

/** What the API returns for a room. Plan-drawing details (rectangle, node...) are left out on purpose. */
@Schema(description = "Fiche d'une salle")
public record RoomDto(
        @Schema(description = "Code de la salle : lettre du bâtiment + 3 chiffres (le premier est l'étage)", example = "A204") String code,
        @Schema(description = "Nom complet de la salle", example = "Salle de TD A204") String name,
        @Schema(description = "Type de salle", example = "Salle de TD") String type,
        @Schema(description = "Code du bâtiment", example = "A") String building,
        @Schema(description = "Étage (0 = rez-de-chaussée)", example = "2") int floor,
        @Schema(description = "Nombre de places", example = "30") int capacity,
        @Schema(description = "Équipements disponibles", example = "[\"Tableau\", \"Vidéoprojecteur\"]") List<String> equipment) {

    public static RoomDto from(Room room) {
        return new RoomDto(room.getCode(), room.getName(), room.getType(), room.getBuilding(), room.getFloor(),
                room.getCapacity(), List.copyOf(room.getEquipment()));
    }
}
