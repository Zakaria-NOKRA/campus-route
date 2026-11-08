package com.campusroute.campus_route_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campusroute.campus_route_backend.dto.PlanDto;
import com.campusroute.campus_route_backend.service.PlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Plan", description = "Données pour dessiner le bâtiment")
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @Operation(summary = "Plan complet du bâtiment",
            description = "Renvoie en un seul appel l'échelle, les étages, les salles (avec leur position), "
                    + "les nœuds et les liens du graphe de navigation : tout ce qu'il faut pour dessiner le plan.")
    @ApiResponse(responseCode = "200", description = "Plan du bâtiment")
    @GetMapping
    public PlanDto plan() {
        return planService.getPlan();
    }
}
