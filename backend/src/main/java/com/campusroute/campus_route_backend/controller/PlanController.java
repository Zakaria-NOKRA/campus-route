package com.campusroute.campus_route_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campusroute.campus_route_backend.dto.ErrorResponse;
import com.campusroute.campus_route_backend.dto.PlanDto;
import com.campusroute.campus_route_backend.service.PlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;

@Tag(name = "Plan", description = "Données pour dessiner un bâtiment")
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    // @NotBlank: an empty "building" gives a 400 (a missing one gives a 400 too, by default)
    @Operation(summary = "Plan d'un bâtiment",
            description = "Renvoie en un seul appel l'échelle, la taille et le contour du plan, les étages, les salles "
                    + "(rectangle et porte), les nœuds et les liens du graphe de navigation de ce bâtiment. "
                    + "building=EXT donne le plan de l'extérieur (cour et allées).")
    @ApiResponse(responseCode = "200", description = "Plan du bâtiment")
    @ApiResponse(responseCode = "400", description = "Paramètre building manquant ou vide",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Aucun bâtiment avec ce code",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public PlanDto plan(
            @Parameter(description = "Code du bâtiment (A, B, C) ou EXT pour l'extérieur", example = "A")
            @RequestParam @NotBlank String building) {
        return planService.getPlan(building);
    }
}
