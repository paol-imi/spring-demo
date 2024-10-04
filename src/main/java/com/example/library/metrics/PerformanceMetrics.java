package com.example.library.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PerformanceMetrics {
    private final MeterRegistry meterRegistry;

    public PerformanceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordMethodExecutionTime(String className, String methodName, long executionTime) {
        Timer.builder("library.method.execution.time")
                .description("Execution time of methods")
                .tag("class", className)
                .tag("method", methodName)
                .register(this.meterRegistry)
                .record(executionTime, TimeUnit.MILLISECONDS);
    }
}
