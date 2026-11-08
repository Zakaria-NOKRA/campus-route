package com.campusroute.campus_route_backend.dto;

import com.campusroute.campus_route_backend.domain.Edge;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lien (non orienté) entre deux nœuds du plan")
public record PlanEdgeDto(
        @Schema(description = "Identifiant du premier nœud", example = "C2_3") String from,
        @Schema(description = "Identifiant du second nœud", example = "B204") String to,
        @Schema(description = "Mode de déplacement : WALK (marche), STAIRS (escalier) ou ELEVATOR (ascenseur)", example = "WALK") String kind) {

    public static PlanEdgeDto from(Edge edge) {
        return new PlanEdgeDto(edge.getA().getId(), edge.getB().getId(), edge.getKind().name());
    }
}
