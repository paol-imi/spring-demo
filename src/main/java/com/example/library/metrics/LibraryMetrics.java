package com.example.library.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class LibraryMetrics {
    private final Counter bookAddedCounter;
    private final Counter bookRemovedCounter;

    public LibraryMetrics(MeterRegistry meterRegistry) {
        this.bookAddedCounter = Counter.builder("library.books.added")
                .description("Total number of books added to the library")
                .tag("timeframe", "total")
                .register(meterRegistry);

        this.bookRemovedCounter = Counter.builder("library.books.removed")
                .description("Total number of books removed from the library")
                .tag("timeframe", "total")
                .register(meterRegistry);
    }

    public void recordBookAdded() {
        this.bookAddedCounter.increment();
    }

    public void recordBookRemoved() {
        this.bookRemovedCounter.increment();
    }
}