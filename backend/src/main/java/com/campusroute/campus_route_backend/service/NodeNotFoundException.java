package com.campusroute.campus_route_backend.service;

/** Thrown when a route starts or ends at a node id that does not exist. */
public class NodeNotFoundException extends RuntimeException {

    public NodeNotFoundException(String id) {
        super("Point " + id + " introuvable");
    }
}
