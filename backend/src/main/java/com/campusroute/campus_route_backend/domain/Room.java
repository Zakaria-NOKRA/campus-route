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

/** A room, with the graph node where its door opens. Code = building letter + floor digit + 2 digits. */
@Entity
@Table(name = "room")
public class Room {

    @Id
    private String code;

    private int floor;

    // Only used to draw the plan: column in the building grid, and top/bottom row
    private int slot;
    @Column(name = "top_row")
    private boolean top;

    private String name;
    private String type;
    private String tag;
    private int capacity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_equipment", joinColumns = @JoinColumn(name = "room_code"))
    @Column(name = "equipment")
    private List<String> equipment = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "node_id")
    private Node node;

    protected Room() { } // required by JPA

    public Room(String code, int floor, int slot, boolean top, String name, String type,
                String tag, int capacity, List<String> equipment, Node node) {
        this.code = code;
        this.floor = floor;
        this.slot = slot;
        this.top = top;
        this.name = name;
        this.type = type;
        this.tag = tag;
        this.capacity = capacity;
        this.equipment = new ArrayList<>(equipment);
        this.node = node;
    }

    public String getCode() { return code; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public boolean isTop() { return top; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getTag() { return tag; }
    public int getCapacity() { return capacity; }
    public List<String> getEquipment() { return equipment; }
    public Node getNode() { return node; }
}
