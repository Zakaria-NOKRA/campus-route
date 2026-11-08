package com.campusroute.campus_route_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.campusroute.campus_route_backend.dto.ErrorResponse;
import com.campusroute.campus_route_backend.service.NoRouteException;
import com.campusroute.campus_route_backend.service.NodeNotFoundException;
import com.campusroute.campus_route_backend.service.RoomNotFoundException;

/** Turns service exceptions into JSON errors, so controllers stay free of error handling. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> roomNotFound(RoomNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> nodeNotFound(NodeNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NoRouteException.class)
    public ResponseEntity<ErrorResponse> noRoute(NoRouteException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // A required parameter is absent, e.g. /api/route?to=B204
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> missingParameter(MissingServletRequestParameterException e) {
        return error(HttpStatus.BAD_REQUEST, "Paramètre obligatoire manquant : " + e.getParameterName());
    }

    // A parameter is present but breaks a constraint (@NotBlank), e.g. /api/route?from=&to=B204
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> invalidParameter(HandlerMethodValidationException e) {
        return error(HttpStatus.BAD_REQUEST, "Les paramètres from et to ne doivent pas être vides");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message));
    }
}
