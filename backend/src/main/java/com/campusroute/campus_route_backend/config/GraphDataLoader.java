package com.campusroute.campus_route_backend.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.campusroute.campus_route_backend.domain.Edge;
import com.campusroute.campus_route_backend.domain.EdgeKind;
import com.campusroute.campus_route_backend.domain.Node;
import com.campusroute.campus_route_backend.domain.NodeType;
import com.campusroute.campus_route_backend.domain.Room;
import com.campusroute.campus_route_backend.repository.EdgeRepository;
import com.campusroute.campus_route_backend.repository.NodeRepository;
import com.campusroute.campus_route_backend.repository.RoomRepository;

import tools.jackson.databind.json.JsonMapper;

/**
 * Imports data/campus_route_graph.json at startup, only when the database is empty.
 */
@Component
public class GraphDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(GraphDataLoader.class);
    private static final String GRAPH_FILE = "data/campus_route_graph.json";

    // Shapes of the JSON file (only the fields we need; unknown ones are ignored)
    record GraphFile(List<RoomJson> rooms, List<NodeJson> nodes, List<EdgeJson> edges) { }
    record RoomJson(String code, int floor, int slot, boolean top, String name, String type,
                    String tag, int cap, List<String> eq) { }
    record NodeJson(String id, int floor, int x, int y, String type, String label) { }
    record EdgeJson(String a, String b, int w, String kind) { }

    private final NodeRepository nodeRepository;
    private final RoomRepository roomRepository;
    private final EdgeRepository edgeRepository;

    public GraphDataLoader(NodeRepository nodeRepository, RoomRepository roomRepository,
                           EdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.roomRepository = roomRepository;
        this.edgeRepository = edgeRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        load();
    }

    /** Public so tests can call it directly. Does nothing if nodes already exist. */
    @Transactional
    public void load() throws IOException {
        if (nodeRepository.count() > 0) {
            log.info("Graph already loaded, skipping import");
            return;
        }

        GraphFile graph;
        try (InputStream in = new ClassPathResource(GRAPH_FILE).getInputStream()) {
            graph = JsonMapper.builder().build().readValue(in, GraphFile.class);
        }

        // 1. nodes first: rooms and edges point to them
        Map<String, Node> nodesById = new HashMap<>();
        for (NodeJson n : graph.nodes()) {
            nodesById.put(n.id(), new Node(n.id(), n.floor(), n.x(), n.y(),
                    NodeType.valueOf(n.type().toUpperCase()), n.label()));
        }
        nodeRepository.saveAll(nodesById.values());

        // 2. rooms: a room shares its code with the node where its door opens
        List<Room> rooms = graph.rooms().stream()
                .map(r -> new Room(r.code(), r.floor(), r.slot(), r.top(), r.name(), r.type(),
                        r.tag(), r.cap(), r.eq(), nodesById.get(r.code())))
                .toList();
        roomRepository.saveAll(rooms);

        // 3. edges
        List<Edge> edges = graph.edges().stream()
                .map(e -> new Edge(nodesById.get(e.a()), nodesById.get(e.b()), e.w(),
                        EdgeKind.valueOf(e.kind().toUpperCase())))
                .toList();
        edgeRepository.saveAll(edges);

        log.info("Graph imported: {} rooms, {} nodes, {} edges",
                rooms.size(), nodesById.size(), edges.size());
    }
}
