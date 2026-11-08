package com.campusroute.campus_route_backend.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusroute.campus_route_backend.domain.Node;
import com.campusroute.campus_route_backend.domain.Room;
import com.campusroute.campus_route_backend.dto.PlanDto;
import com.campusroute.campus_route_backend.dto.PlanEdgeDto;
import com.campusroute.campus_route_backend.dto.PlanRoomDto;
import com.campusroute.campus_route_backend.dto.RouteNodeDto;
import com.campusroute.campus_route_backend.repository.EdgeRepository;
import com.campusroute.campus_route_backend.repository.NodeRepository;
import com.campusroute.campus_route_backend.repository.RoomRepository;

/** Builds the whole plan (rooms, nodes, edges) so the front end can draw the building with one call. */
// @Transactional: Room.node and Edge.a/b are lazy, they must be read inside a transaction
@Service
@Transactional(readOnly = true)
public class PlanService {

    private final RoomRepository roomRepository;
    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;

    public PlanService(RoomRepository roomRepository, NodeRepository nodeRepository,
                       EdgeRepository edgeRepository) {
        this.roomRepository = roomRepository;
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    public PlanDto getPlan() {
        List<Node> nodes = nodeRepository.findAll();

        // Floors come from the nodes, so they follow the data instead of being hard-coded
        List<Integer> floors = nodes.stream().map(Node::getFloor).distinct().sorted().toList();

        List<PlanRoomDto> rooms = roomRepository.findAll().stream()
                .sorted(Comparator.comparing(Room::getCode))
                .map(PlanRoomDto::from)
                .toList();

        List<RouteNodeDto> nodeDtos = nodes.stream().map(RouteNodeDto::from).toList();
        List<PlanEdgeDto> edges = edgeRepository.findAll().stream().map(PlanEdgeDto::from).toList();

        return new PlanDto(RouteConstants.METERS_PER_PIXEL, floors, rooms, nodeDtos, edges);
    }
}
