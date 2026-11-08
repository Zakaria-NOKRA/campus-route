package com.campusroute.campus_route_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** One instruction of the route, in French. "sub" is a short complement (floor, room name...). */
@Schema(description = "Une instruction de l'itinéraire")
public record RouteStepDto(
        @Schema(description = "Instruction principale, en français", example = "Montez par l'escalier jusqu'au 2e étage") String text,
        @Schema(description = "Complément court (étage, nom de la salle...)", example = "RDC → 2e étage") String sub,
        @Schema(description = "Étage où se déroule l'instruction", example = "0") int floor,
        @Schema(description = "Symbole à afficher : ● départ, ↑ tout droit, ↱ droite, ↰ gauche, ↶ demi-tour, ↕ changement d'étage, ⚑ arrivée", example = "↕") String icon) { }
