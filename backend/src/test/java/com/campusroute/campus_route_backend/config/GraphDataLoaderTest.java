package com.campusroute.campus_route_backend.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.campusroute.campus_route_backend.domain.Room;
import com.campusroute.campus_route_backend.repository.EdgeRepository;
import com.campusroute.campus_route_backend.repository.NodeRepository;
import com.campusroute.campus_route_backend.repository.RoomRepository;

@DataJpaTest
@Import(GraphDataLoader.class)
class GraphDataLoaderTest {

    @Autowired GraphDataLoader loader;
    @Autowired RoomRepository rooms;
    @Autowired NodeRepository nodes;
    @Autowired EdgeRepository edges;

    @Test
    void importsTheWholeGraph() throws IOException {
        loader.load();

        assertThat(rooms.count()).isEqualTo(27);
        assertThat(nodes.count()).isEqualTo(49);
        assertThat(edges.count()).isEqualTo(50);

        Room b204 = rooms.findById("B204").orElseThrow();
        assertThat(b204.getFloor()).isEqualTo(2);
        assertThat(b204.getNode().getId()).isEqualTo("B204");
        assertThat(rooms.findById("B001").orElseThrow().getEquipment()).contains("Sonorisation");
    }

    @Test
    void secondRunDoesNotDuplicate() throws IOException {
        loader.load();
        loader.load();

        assertThat(rooms.count()).isEqualTo(27);
        assertThat(nodes.count()).isEqualTo(49);
        assertThat(edges.count()).isEqualTo(50);
    }
}
