package com.campusroute.campus_route_backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * An undirected link between two nodes: stored once, the route service will
 * walk it in both directions.
 */
@Entity
@Table(name = "edge")
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "node_a_id")
    private Node a;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "node_b_id")
    private Node b;

    // Walking cost in plan pixels (the JSON field is called "w")
    private int weight;

    @Enumerated(EnumType.STRING)
    private EdgeKind kind;

    protected Edge() { } // required by JPA

    public Edge(Node a, Node b, int weight, EdgeKind kind) {
        this.a = a;
        this.b = b;
        this.weight = weight;
        this.kind = kind;
    }

    public Long getId() { return id; }
    public Node getA() { return a; }
    public Node getB() { return b; }
    public int getWeight() { return weight; }
    public EdgeKind getKind() { return kind; }
}
