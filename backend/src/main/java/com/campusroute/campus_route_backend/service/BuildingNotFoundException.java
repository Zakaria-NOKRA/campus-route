package com.campusroute.campus_route_backend.service;

/** Thrown when a building code does not exist. The controller layer turns it into a 404. */
public class BuildingNotFoundException extends RuntimeException {

    public BuildingNotFoundException(String code) {
        super("Bâtiment " + code + " introuvable");
    }
}
