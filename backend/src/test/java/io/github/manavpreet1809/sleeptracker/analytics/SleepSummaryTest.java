package io.github.manavpreet1809.sleeptracker.analytics;

import static io.github.manavpreet1809.sleeptracker.analytics.Nights.night;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class SleepSummaryTest {

    @Test
    void averagesEveryFactorAndUsesClockAwareTimes() {
        SleepSummary summary = SleepSummary.of(List.of(
                night().quality(7).sleepHours(6).bedtime("23:30").log(),
                night().quality(3).sleepHours(8).bedtime("00:30").log()));
        assertEquals(2, summary.nights());
        assertEquals(5, summary.average(SleepFactor.QUALITY));
        assertEquals(7, summary.average(SleepFactor.SLEEP_DURATION));
        assertEquals(LocalTime.MIDNIGHT, summary.bedtime());
    }

    @Test
    void neverRecordedFactorHasNoAverage() {
        SleepSummary summary = SleepSummary.of(List.of(night().water(null).log()));
        assertNull(summary.average(SleepFactor.WATER_INTAKE));
    }
}
