package com.campusroute.campus_route_backend.dto;

import java.util.List;

import com.campusroute.campus_route_backend.domain.Building;

import io.swagger.v3.oas.annotations.media.Schema;

/** One entry of GET /api/buildings: a building, or the outdoor space (EXT). */
@Schema(description = "Bâtiment du campus, ou l'espace extérieur (code EXT)")
public record BuildingDto(
        @Schema(description = "Code du bâtiment ; EXT pour l'extérieur", example = "A") String code,
        @Schema(description = "Nom affichable", example = "Bâtiment A – Sciences") String name,
        @Schema(description = "true pour l'espace extérieur (cour, allées)", example = "false") boolean outdoor,
        @Schema(description = "Étages, triés", example = "[0, 1, 2]") List<Integer> floors,
        @Schema(description = "Largeur du plan du bâtiment, en pixels", example = "1000") int width,
        @Schema(description = "Hauteur du plan du bâtiment, en pixels", example = "800") int height,
        @Schema(description = "Contour du bâtiment sur son propre plan (attribut SVG points)", example = "0,0 1000,0 1000,800 700,800 700,300 0,300") String outline,
        @Schema(description = "Contour du bâtiment sur le plan du campus (attribut SVG points)", example = "200,200 1200,200 1200,1000 900,1000 900,500 200,500") String siteOutline) {

    public static BuildingDto from(Building building, List<Integer> floors) {
        return new BuildingDto(building.getCode(), building.getName(), building.isOutdoor(), floors,
                building.getWidth(), building.getHeight(), building.getOutline(), building.getSiteOutline());
    }
}
