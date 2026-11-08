package com.campusroute.campus_route_backend.service;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusroute.campus_route_backend.dto.RoomDto;
import com.campusroute.campus_route_backend.repository.RoomRepository;

@Service
@Transactional(readOnly = true)
public class RoomService {

    static final int MAX_RESULTS = 8;

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /** Search by code, name or type: case and spaces ignored, 8 results max, sorted by code. */
    public List<RoomDto> search(String q) {
        String normalized = q == null ? "" : q.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
        if (normalized.isEmpty()) {
            return List.of();
        }
        // Escape LIKE wildcards so that "%" and "_" are searched literally
        String escaped = normalized.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return roomRepository.search("%" + escaped + "%", PageRequest.of(0, MAX_RESULTS)).stream()
                .map(RoomDto::from)
                .toList();
    }

    public RoomDto findByCode(String code) {
        String normalized = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        return roomRepository.findById(normalized)
                .map(RoomDto::from)
                .orElseThrow(() -> new RoomNotFoundException(normalized));
    }
}
