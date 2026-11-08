package com.campusroute.campus_route_backend.service;

/** Thrown when two existing nodes are not connected (with the requested options). */
public class NoRouteException extends RuntimeException {

    public NoRouteException(String from, String to, boolean accessible) {
        super("Aucun itinéraire entre " + from + " et " + to + (accessible ? " sans escalier" : ""));
    }
}
