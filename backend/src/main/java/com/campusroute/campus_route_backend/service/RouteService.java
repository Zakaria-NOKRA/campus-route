package com.campusroute.campus_route_backend.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusroute.campus_route_backend.domain.Edge;
import com.campusroute.campus_route_backend.domain.EdgeKind;
import com.campusroute.campus_route_backend.domain.Node;
import com.campusroute.campus_route_backend.domain.Room;
import com.campusroute.campus_route_backend.dto.RouteDto;
import com.campusroute.campus_route_backend.dto.RouteNodeDto;
import com.campusroute.campus_route_backend.dto.RouteStepDto;
import com.campusroute.campus_route_backend.repository.EdgeRepository;
import com.campusroute.campus_route_backend.repository.NodeRepository;
import com.campusroute.campus_route_backend.repository.RoomRepository;

/** Reads the graph, asks the pathfinder for the path, then adds distance, duration and instructions. */
@Service
@Transactional(readOnly = true)
public class RouteService {

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final RoomRepository roomRepository;
    private final PathfinderService pathfinder;
    private final InstructionService instructions;

    public RouteService(NodeRepository nodeRepository, EdgeRepository edgeRepository,
                        RoomRepository roomRepository, PathfinderService pathfinder,
                        InstructionService instructions) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.roomRepository = roomRepository;
        this.pathfinder = pathfinder;
        this.instructions = instructions;
    }

    public RouteDto findRoute(String from, String to, boolean accessible) {
        String fromId = normalize(from);
        String toId = normalize(to);

        PathfinderService.PathResult result = pathfinder.shortestPath(
                nodeRepository.findAll(), edgeRepository.findAll(), fromId, toId, accessible);

        // Only WALK edges count for the distance; stairs and elevator add a fixed time instead
        int walkPixels = 0;
        int stairsEdges = 0;
        int elevatorEdges = 0;
        for (Edge e : result.edges()) {
            if (e.getKind() == EdgeKind.WALK) {
                walkPixels += e.getWeight();
            } else if (e.getKind() == EdgeKind.STAIRS) {
                stairsEdges++;
            } else {
                elevatorEdges++;
            }
        }
        double meters = walkPixels * RouteConstants.METERS_PER_PIXEL;
        double seconds = meters / RouteConstants.WALKING_SPEED_MPS
                + stairsEdges * RouteConstants.STAIRS_SECONDS
                + elevatorEdges * RouteConstants.ELEVATOR_SECONDS;

        List<Node> nodes = result.nodes();
        String destinationName = roomRepository.findById(toId).map(Room::getName).orElse(null);
        List<RouteStepDto> steps = instructions.build(nodes, destinationName);

        return new RouteDto(round(meters, 100), round(seconds, 10),
                nodes.stream().map(RouteNodeDto::from).toList(), steps);
    }

    private String normalize(String id) {
        return id == null ? "" : id.trim().toUpperCase(Locale.ROOT);
    }

    // factor 100 keeps 2 decimals, factor 10 keeps 1
    private double round(double value, int factor) {
        return Math.round(value * factor) / (double) factor;
    }
}
