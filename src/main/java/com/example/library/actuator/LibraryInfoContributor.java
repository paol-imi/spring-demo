package com.example.library.actuator;

import com.example.library.repository.BookRepository;
import com.example.library.repository.LocationRepository;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class LibraryInfoContributor implements InfoContributor {

    private final BookRepository bookRepository;
    private final LocationRepository locationRepository;

    public LibraryInfoContributor(BookRepository bookRepository, LocationRepository locationRepository) {
        this.bookRepository = bookRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("library_stats",
                java.util.Map.of(
                        "total_books", this.bookRepository.count(),
                        "total_locations", this.locationRepository.count()
                )
        );
    }
}