package com.campusroute.campus_route_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campusroute.campus_route_backend.dto.ErrorResponse;
import com.campusroute.campus_route_backend.dto.RouteDto;
import com.campusroute.campus_route_backend.service.RouteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;

@Tag(name = "Itinéraire", description = "Calcul du chemin le plus court entre deux points du bâtiment")
@RestController
@RequestMapping("/api/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // @NotBlank: an empty "from" or "to" gives a 400 (a missing one gives a 400 too, by default)
    @Operation(summary = "Calculer un itinéraire",
            description = "Calcule le chemin le plus court (Dijkstra) entre deux nœuds du graphe, avec la distance, "
                    + "la durée estimée et des instructions pas à pas en français. "
                    + "Avec accessible=true, les escaliers sont évités (ascenseur uniquement).")
    @ApiResponse(responseCode = "200", description = "Itinéraire calculé")
    @ApiResponse(responseCode = "400", description = "Paramètre manquant ou vide",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Point de départ ou d'arrivée inconnu, ou aucun chemin possible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public RouteDto route(
            @Parameter(description = "Identifiant du point de départ : code de salle ou ENTREE pour l'entrée principale", example = "ENTREE")
            @RequestParam @NotBlank String from,
            @Parameter(description = "Identifiant du point d'arrivée : code de salle", example = "B204")
            @RequestParam @NotBlank String to,
            @Parameter(description = "true pour un itinéraire sans escaliers", example = "false")
            @RequestParam(defaultValue = "false") boolean accessible) {
        return routeService.findRoute(from, to, accessible);
    }
}
