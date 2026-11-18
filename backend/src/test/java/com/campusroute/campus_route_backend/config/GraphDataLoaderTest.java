package com.campusroute.campus_route_backend.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.campusroute.campus_route_backend.domain.Building;
import com.campusroute.campus_route_backend.domain.NodeType;
import com.campusroute.campus_route_backend.domain.Room;
import com.campusroute.campus_route_backend.repository.BuildingRepository;
import com.campusroute.campus_route_backend.repository.EdgeRepository;
import com.campusroute.campus_route_backend.repository.NodeRepository;
import com.campusroute.campus_route_backend.repository.RoomRepository;

// Counts come from "node tools/campus/generate.mjs" (it prints them)
@DataJpaTest
@Import(GraphDataLoader.class)
class GraphDataLoaderTest {

    @Autowired GraphDataLoader loader;
    @Autowired BuildingRepository buildings;
    @Autowired RoomRepository rooms;
    @Autowired NodeRepository nodes;
    @Autowired EdgeRepository edges;

    @Test
    void importsTheWholeCampus() throws IOException {
        loader.load();

        assertThat(buildings.findAll()).extracting(Building::getCode).containsExactlyInAnyOrder("A", "B", "C", "EXT");
        assertThat(rooms.count()).isEqualTo(77);
        assertThat(nodes.count()).isEqualTo(251);
        assertThat(edges.count()).isEqualTo(264);
    }

    @Test
    void roomHasItsBuildingRectangleAndDoorNode() throws IOException {
        loader.load();

        Room a204 = rooms.findById("A204").orElseThrow();
        assertThat(a204.getBuilding()).isEqualTo("A");
        assertThat(a204.getFloor()).isEqualTo(2);
        assertThat(a204.getX()).isEqualTo(750);
        assertThat(a204.getY()).isEqualTo(20);
        assertThat(a204.getWidth()).isEqualTo(230);
        assertThat(a204.getHeight()).isEqualTo(100);
        assertThat(a204.getNode().getId()).isEqualTo("A204");
        assertThat(rooms.findById("B001").orElseThrow().getEquipment()).contains("Sonorisation");
    }

    @Test
    void outdoorSpaceAndEntrance() throws IOException {
        loader.load();

        assertThat(buildings.findById("EXT").orElseThrow().isOutdoor()).isTrue();
        assertThat(buildings.findById("A").orElseThrow().isOutdoor()).isFalse();
        var entrance = nodes.findById("ENTREE").orElseThrow();
        assertThat(entrance.getBuilding()).isEqualTo("EXT");
        assertThat(entrance.getType()).isEqualTo(NodeType.ENTRANCE);
        assertThat(nodes.findById("EXT-P2").orElseThrow().getType()).isEqualTo(NodeType.OUTDOOR);
    }

    @Test
    void secondRunDoesNotDuplicate() throws IOException {
        loader.load();
        loader.load();

        assertThat(buildings.count()).isEqualTo(4);
        assertThat(rooms.count()).isEqualTo(77);
        assertThat(nodes.count()).isEqualTo(251);
        assertThat(edges.count()).isEqualTo(264);
    }
}
