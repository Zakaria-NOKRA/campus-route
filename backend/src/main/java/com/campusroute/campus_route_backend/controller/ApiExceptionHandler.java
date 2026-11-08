package com.campusroute.campus_route_backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.campusroute.campus_route_backend.service.NoRouteException;
import com.campusroute.campus_route_backend.service.NodeNotFoundException;
import com.campusroute.campus_route_backend.service.RoomNotFoundException;

/** Turns service exceptions into JSON errors, so controllers stay free of error handling. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<Map<String, Object>> roomNotFound(RoomNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> nodeNotFound(NodeNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NoRouteException.class)
    public ResponseEntity<Map<String, Object>> noRoute(NoRouteException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // A required parameter is absent, e.g. /api/route?to=B204
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> missingParameter(MissingServletRequestParameterException e) {
        return error(HttpStatus.BAD_REQUEST, "Paramètre obligatoire manquant : " + e.getParameterName());
    }

    // A parameter is present but breaks a constraint (@NotBlank), e.g. /api/route?from=&to=B204
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> invalidParameter(HandlerMethodValidationException e) {
        return error(HttpStatus.BAD_REQUEST, "Les paramètres from et to ne doivent pas être vides");
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("status", status.value(), "message", message));
    }
}
