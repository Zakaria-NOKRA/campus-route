package com.campusroute.campus_route_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campusroute.campus_route_backend.dto.RouteDto;
import com.campusroute.campus_route_backend.service.RouteService;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // @NotBlank: an empty "from" or "to" gives a 400 (a missing one gives a 400 too, by default)
    @GetMapping
    public RouteDto route(@RequestParam @NotBlank String from,
                          @RequestParam @NotBlank String to,
                          @RequestParam(defaultValue = "false") boolean accessible) {
        return routeService.findRoute(from, to, accessible);
    }
}
