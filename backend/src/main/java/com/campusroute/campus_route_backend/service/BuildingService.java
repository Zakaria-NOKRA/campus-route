package com.campusroute.campus_route_backend.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusroute.campus_route_backend.dto.BuildingDto;
import com.campusroute.campus_route_backend.repository.BuildingRepository;
import com.campusroute.campus_route_backend.repository.NodeRepository;

/** List of the plan spaces of the campus: buildings A, B, C and the outdoor space EXT. */
@Service
@Transactional(readOnly = true)
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final NodeRepository nodeRepository;

    public BuildingService(BuildingRepository buildingRepository, NodeRepository nodeRepository) {
        this.buildingRepository = buildingRepository;
        this.nodeRepository = nodeRepository;
    }

    /** All buildings sorted by code (A, B, C, EXT), each with its floors. */
    public List<BuildingDto> findAll() {
        return buildingRepository.findAll(Sort.by("code")).stream()
                .map(b -> BuildingDto.from(b, nodeRepository.findFloorsByBuilding(b.getCode())))
                .toList();
    }
}
