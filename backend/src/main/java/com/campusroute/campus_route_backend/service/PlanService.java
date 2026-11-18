package com.campusroute.campus_route_backend.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusroute.campus_route_backend.domain.Building;
import com.campusroute.campus_route_backend.dto.PlanDto;
import com.campusroute.campus_route_backend.dto.PlanEdgeDto;
import com.campusroute.campus_route_backend.dto.PlanRoomDto;
import com.campusroute.campus_route_backend.dto.RouteNodeDto;
import com.campusroute.campus_route_backend.repository.BuildingRepository;
import com.campusroute.campus_route_backend.repository.EdgeRepository;
import com.campusroute.campus_route_backend.repository.NodeRepository;
import com.campusroute.campus_route_backend.repository.RoomRepository;

/** Builds the plan of one building (rooms, nodes, edges) so the front end can draw it with one call. */
// @Transactional: Room.node is lazy, it must be read inside a transaction
@Service
@Transactional(readOnly = true)
public class PlanService {

    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;

    public PlanService(BuildingRepository buildingRepository, RoomRepository roomRepository,
                       NodeRepository nodeRepository, EdgeRepository edgeRepository) {
        this.buildingRepository = buildingRepository;
        this.roomRepository = roomRepository;
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    /** Plan of one building, or of the outdoor space with "EXT". Case and spaces around are ignored. */
    public PlanDto getPlan(String buildingCode) {
        String code = buildingCode == null ? "" : buildingCode.trim().toUpperCase(Locale.ROOT);
        Building building = buildingRepository.findById(code)
                .orElseThrow(() -> new BuildingNotFoundException(code));

        List<PlanRoomDto> rooms = roomRepository.findByBuildingOrderByCode(code).stream()
                .map(PlanRoomDto::from)
                .toList();
        List<RouteNodeDto> nodes = nodeRepository.findByBuilding(code).stream()
                .map(RouteNodeDto::from)
                .toList();
        List<PlanEdgeDto> edges = edgeRepository.findInsideBuilding(code).stream()
                .map(PlanEdgeDto::from)
                .toList();

        return new PlanDto(building.getCode(), building.getName(), RouteConstants.METERS_PER_PIXEL,
                building.getWidth(), building.getHeight(), building.getOutline(),
                nodeRepository.findFloorsByBuilding(code), rooms, nodes, edges);
    }
}
