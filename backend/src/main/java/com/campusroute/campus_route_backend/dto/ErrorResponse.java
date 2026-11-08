package com.campusroute.campus_route_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** JSON body of every error answer of the API. */
@Schema(description = "Erreur renvoyée par l'API")
public record ErrorResponse(
        @Schema(description = "Code HTTP de l'erreur", example = "404") int status,
        @Schema(description = "Message lisible, en français", example = "Salle B999 introuvable") String message) { }
