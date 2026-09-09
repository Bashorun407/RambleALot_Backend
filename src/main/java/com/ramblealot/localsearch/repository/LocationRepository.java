package com.ramblealot.localsearch.repository;

import com.ramblealot.localsearch.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query(value = """
            SELECT * FROM locations l 
            WHERE ST_DWithin(
                l.coordinates\\:\\:geography, 
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)\\:\\:geography, 
                :radiusInMeters
            ) AND l.is_active = true
            """, nativeQuery = true)
    List<Location> findLocationsWithinRadius(
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radiusInMeters") double radiusInMeters
    );
}
