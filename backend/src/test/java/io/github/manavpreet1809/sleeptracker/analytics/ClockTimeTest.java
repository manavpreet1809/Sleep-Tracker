package io.github.manavpreet1809.sleeptracker.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClockTimeTest {

    private static LocalTime hours(double value) {
        return ClockTime.fromHours(value);
    }

    @Test
    void formatsDecimalHours() {
        assertEquals(LocalTime.of(22, 30), hours(22.5));
        assertEquals(LocalTime.MIDNIGHT, hours(0));
        assertEquals(LocalTime.of(1, 0), hours(25));
    }

    @Test
    void averageCrossingMidnight() {
        assertEquals(23.90, ClockTime.meanHours(List.of(hours(23.75), hours(0.05))), 0.01);
    }

    @Test
    void averageCrossingMidnightAcrossFiveNights() {
        assertEquals(0.57, ClockTime.meanHours(
                List.of(hours(23.75), hours(0.05), hours(3.05), hours(1.99), hours(22.01))), 0.01);
    }

    @Test
    void averageWithoutCrossingMidnight() {
        assertEquals(22.29, ClockTime.meanHours(
                List.of(hours(23.75), hours(23.05), hours(21.11), hours(20.99), hours(22.55))), 0.01);
    }

    @Test
    void varianceMeasuresShortestWayAroundTheClock() {
        // 23:30 and 00:30 are one hour apart: deviations of ±0.5 h give sample variance 0.5.
        assertEquals(0.5, ClockTime.sampleVarianceHours(List.of(LocalTime.of(23, 30), LocalTime.of(0, 30))), 1e-9);
    }

    @Test
    void varianceAroundMiddayIsNotInflated() {
        // The original app added 24 h to times before noon, so 11:30 and 12:30 looked 23 hours apart.
        assertEquals(0.5, ClockTime.sampleVarianceHours(List.of(LocalTime.of(11, 30), LocalTime.of(12, 30))), 1e-9);
    }
}
