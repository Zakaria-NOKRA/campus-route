package com.campusroute.campus_route_backend.service;

/** Thrown when a room code does not exist. The controller layer turns it into a 404. */
public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(String code) {
        super("Salle " + code + " introuvable");
    }
}
