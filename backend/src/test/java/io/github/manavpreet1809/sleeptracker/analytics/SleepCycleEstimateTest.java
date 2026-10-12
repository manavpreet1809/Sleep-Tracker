package io.github.manavpreet1809.sleeptracker.analytics;

import static io.github.manavpreet1809.sleeptracker.analytics.Nights.night;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SleepCycleEstimateTest {

    @Test
    void ninetyMinuteCyclesForAverageQuality() {
        assertEquals(6, SleepCycleEstimate.of(night().quality(5).sleepHours(9).log()).cycles());
        assertEquals(5, SleepCycleEstimate.of(night().quality(5).sleepHours(7.5).log()).cycles());
    }

    @Test
    void restfulNightsHaveShorterCycles() {
        SleepCycleEstimate estimate = SleepCycleEstimate.of(night().quality(9).sleepHours(7).log());
        assertEquals(84, estimate.cycleMinutes(), 1e-9);
        assertEquals(5, estimate.cycles());
    }

    @Test
    void deepSleepGrowsWithQuality() {
        double good = SleepCycleEstimate.of(night().quality(9).sleepHours(8).log()).deepSleepMinutes();
        double poor = SleepCycleEstimate.of(night().quality(3).sleepHours(8).log()).deepSleepMinutes();
        assertTrue(good > 50);
        assertTrue(poor > 40);
        assertTrue(good > poor);
    }

    @Test
    void remShareMatchesFormula() {
        // 8 h at quality 5: REM share 0.2 + 5/50 = 30% of 480 min.
        assertEquals(144, SleepCycleEstimate.of(night().quality(5).sleepHours(8).log()).remMinutes(), 1e-9);
    }
}
