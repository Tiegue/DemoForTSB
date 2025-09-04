package nz.co.tsb.demofortsb.healthCheck;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MetricService {

    private final MeterRegistry meterRegistry;

    public MetricService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Records both count and duration metrics for a business operation.
     *
     * @param operationName the business operation identifier
     * @param durationMs    execution time in milliseconds
     * @param success       true if operation succeeded, false if failed
     */
    public void recordOperation(String operationName, long durationMs, boolean success) {
        String status = success ? "success" : "failure";

        // Counter: total number of operations
        meterRegistry.counter(
                "business_operation_total",
                "operation", operationName,
                "status", status
        ).increment();

        // Timer: records duration (Micrometer exports
        Timer.builder("business_operation_duration_seconds")
                .tags("operation", operationName, "status", status)
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);

    }
}
