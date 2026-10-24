package com.ramblealot.localsearch.bootstrap;

import com.ramblealot.localsearch.model.Location;
import com.ramblealot.localsearch.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseDataSeeder implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public void run(String... args) {
        if (locationRepository.count() >= 300) {
            log.info("Database already contains {} locations. Skipping seeding.", locationRepository.count());
            return;
        }

        log.info("Seeding 300+ sample locations for MVP launch...");
        List<Location> seedLocations = new ArrayList<>();

        // Base coordinates (e.g., pilot city center: Longitude 3.3792, Latitude 6.5244)
        double baseLon = 3.3792;
        double baseLat = 6.5244;

        for (int i = 1; i <= 320; i++) {
            // Distribute points within roughly a 5-10km radius
            double offsetLon = (Math.random() - 0.5) * 0.08;
            double offsetLat = (Math.random() - 0.5) * 0.08;

            // Option A: Dynamic placeholder for UI testing variety
            String sampleImageUrl = "https://picsum.photos/seed/" + i + "/400/300";

            // Option B: Point to a default image in your new S3 bucket
            // String sampleImageUrl = "https://ramblealot-assets-olusheyi-2026.s3.amazonaws.com/default-location.jpg";

            Location loc = Location.builder()
                    .name("Pilot Exploration Spot #" + i)
                    .imageUrl(sampleImageUrl)
                    .description("Realistic community exploration challenge and landmark spot #" + i)
                    .openingHours("08:00 AM - 08:00 PM")
                    .coordinates(geometryFactory.createPoint(new Coordinate(baseLon + offsetLon, baseLat + offsetLat)))
                    .isActive(true)
                    .build();

            seedLocations.add(loc);
        }

        locationRepository.saveAll(seedLocations);
        log.info("Successfully seeded {} locations into the database.", seedLocations.size());
    }
}
