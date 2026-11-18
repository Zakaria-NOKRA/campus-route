package com.campusroute.campus_route_backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A "plan space": a building (A, B, C) or the outdoor site (EXT). Every node belongs to one of them,
 * and its x/y are measured in that space (1 px = 0.07 m).
 */
@Entity
@Table(name = "building")
public class Building {

    @Id
    private String code;

    private String name;

    // true for the outdoor site (courtyard, paths), false for a real building
    private boolean outdoor;

    // Size of the plan, in pixels
    private int width;
    private int height;

    // Position of the building's (0,0) on the site plan
    private int originX;
    private int originY;

    // Polygons as SVG "points" strings: "x1,y1 x2,y2 ..." (building plan, then site plan)
    @Column(length = 1000)
    private String outline;
    @Column(length = 1000)
    private String siteOutline;

    protected Building() { } // required by JPA

    public Building(String code, String name, boolean outdoor, int width, int height,
                    int originX, int originY, String outline, String siteOutline) {
        this.code = code;
        this.name = name;
        this.outdoor = outdoor;
        this.width = width;
        this.height = height;
        this.originX = originX;
        this.originY = originY;
        this.outline = outline;
        this.siteOutline = siteOutline;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public boolean isOutdoor() { return outdoor; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getOriginX() { return originX; }
    public int getOriginY() { return originY; }
    public String getOutline() { return outline; }
    public String getSiteOutline() { return siteOutline; }
}
