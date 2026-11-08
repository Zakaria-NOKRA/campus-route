package com.campusroute.campus_route_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campusroute.campus_route_backend.dto.RoomDto;
import com.campusroute.campus_route_backend.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/search")
    public List<RoomDto> search(@RequestParam(defaultValue = "") String q) {
        return roomService.search(q);
    }

    @GetMapping("/{code}")
    public RoomDto get(@PathVariable String code) {
        return roomService.findByCode(code);
    }
}
