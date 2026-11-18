package com.campusroute.campus_route_backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A point of the navigation graph. The id is a business key such as "A1-K", "A104_C" or "A104". */
@Entity
@Table(name = "node")
public class Node {

    @Id
    private String id;

    // Plan space of the node: building code (A, B, C) or EXT for outdoor points
    private String building;

    private int floor;

    // Position on the plan of its building and floor, in pixels (1 px = 0.07 m)
    private int x;
    private int y;

    @Enumerated(EnumType.STRING)
    private NodeType type;

    private String label;

    protected Node() { } // required by JPA

    public Node(String id, String building, int floor, int x, int y, NodeType type, String label) {
        this.id = id;
        this.building = building;
        this.floor = floor;
        this.x = x;
        this.y = y;
        this.type = type;
        this.label = label;
    }

    public String getId() { return id; }
    public String getBuilding() { return building; }
    public int getFloor() { return floor; }
    public int getX() { return x; }
    public int getY() { return y; }
    public NodeType getType() { return type; }
    public String getLabel() { return label; }
}
