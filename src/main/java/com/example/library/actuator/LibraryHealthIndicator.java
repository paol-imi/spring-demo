package com.example.library.actuator;

import com.example.library.repository.BookRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class LibraryHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    public LibraryHealthIndicator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Health health() {
        long bookCount = this.bookRepository.count();
        if (bookCount > 0) {
            return Health.up()
                    .withDetail("bookCount", bookCount)
                    .build();
        } else {
            return Health.down()
                    .withDetail("reason", "No books in the library")
                    .build();
        }
    }
}