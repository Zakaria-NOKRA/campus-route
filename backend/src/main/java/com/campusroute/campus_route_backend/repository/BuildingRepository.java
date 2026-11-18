package com.campusroute.campus_route_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campusroute.campus_route_backend.domain.Building;

public interface BuildingRepository extends JpaRepository<Building, String> {
}
