package com.campusroute.campus_route_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campusroute.campus_route_backend.dto.BuildingDto;
import com.campusroute.campus_route_backend.service.BuildingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Bâtiments", description = "Liste des bâtiments du campus")
@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @Operation(summary = "Liste des bâtiments",
            description = "Renvoie les bâtiments triés par code, avec leurs étages et leurs contours, "
                    + "puis l'espace extérieur (code EXT) qui relie les bâtiments.")
    @ApiResponse(responseCode = "200", description = "Bâtiments du campus")
    @GetMapping
    public List<BuildingDto> list() {
        return buildingService.findAll();
    }
}
