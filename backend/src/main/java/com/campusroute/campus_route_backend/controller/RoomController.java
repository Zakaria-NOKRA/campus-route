package com.campusroute.campus_route_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campusroute.campus_route_backend.dto.ErrorResponse;
import com.campusroute.campus_route_backend.dto.RoomDto;
import com.campusroute.campus_route_backend.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Salles", description = "Recherche et fiche des salles du bâtiment")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Rechercher des salles",
            description = "Cherche dans le code, le nom et le type des salles, sans tenir compte de la casse ni des espaces. "
                    + "Renvoie 8 résultats au maximum, triés par code. Une recherche vide renvoie une liste vide.")
    @ApiResponse(responseCode = "200", description = "Salles trouvées (liste éventuellement vide)")
    @ApiResponse(responseCode = "400", description = "Requête invalide",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/search")
    public List<RoomDto> search(
            @Parameter(description = "Texte recherché : code, nom ou type de salle", example = "b204")
            @RequestParam(defaultValue = "") String q) {
        return roomService.search(q);
    }

    @Operation(summary = "Fiche d'une salle",
            description = "Renvoie les informations d'une salle à partir de son code (casse et espaces autour ignorés).")
    @ApiResponse(responseCode = "200", description = "Salle trouvée")
    @ApiResponse(responseCode = "400", description = "Requête invalide",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Aucune salle avec ce code",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{code}")
    public RoomDto get(
            @Parameter(description = "Code de la salle : lettre du bâtiment + 3 chiffres", example = "B204")
            @PathVariable String code) {
        return roomService.findByCode(code);
    }
}
