package com.ramblealot.localsearch.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "opening_hours")
    private String openingHours;

    // Spatial data mapping for PostGIS (SRID 4326 standardizes to GPS Long/Lat)
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point coordinates;

    private boolean isActive = true;
}
