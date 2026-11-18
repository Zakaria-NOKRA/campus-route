package com.campusroute.campus_route_backend.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * A room, with the graph node of its door. Code = building letter + floor digit + 2 digits (A204).
 * The room is drawn as a rectangle (x, y, width, height) on the plan of its building and floor.
 */
@Entity
@Table(name = "room")
public class Room {

    @Id
    private String code;

    private String building;
    private int floor;

    // Rectangle on the plan, in pixels: top-left corner and size
    private int x;
    private int y;
    private int width;
    private int height;

    private String name;
    private String type;
    private String tag;
    private int capacity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_equipment", joinColumns = @JoinColumn(name = "room_code"))
    @Column(name = "equipment")
    private List<String> equipment = new ArrayList<>();

    // The node of the door: it has the same id as the room code
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "node_id")
    private Node node;

    protected Room() { } // required by JPA

    public Room(String code, String building, int floor, int x, int y, int width, int height,
                String name, String type, String tag, int capacity, List<String> equipment, Node node) {
        this.code = code;
        this.building = building;
        this.floor = floor;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.type = type;
        this.tag = tag;
        this.capacity = capacity;
        this.equipment = new ArrayList<>(equipment);
        this.node = node;
    }

    public String getCode() { return code; }
    public String getBuilding() { return building; }
    public int getFloor() { return floor; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getTag() { return tag; }
    public int getCapacity() { return capacity; }
    public List<String> getEquipment() { return equipment; }
    public Node getNode() { return node; }
}
