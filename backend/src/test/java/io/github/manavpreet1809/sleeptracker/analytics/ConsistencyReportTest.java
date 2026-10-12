package io.github.manavpreet1809.sleeptracker.analytics;

import static io.github.manavpreet1809.sleeptracker.analytics.Nights.night;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ConsistencyReportTest {

    @Test
    void needsThreeNights() {
        assertTrue(ConsistencyReport.of(List.of(night().log(), night().log())).isEmpty());
    }

    @Test
    void identicalNightsAreFullyConsistent() {
        ConsistencyReport report = ConsistencyReport.of(List.of(night().log(), night().log(), night().log())).orElseThrow();
        assertEquals(100, report.overall());
        assertEquals("Excellent", report.rating());
    }

    @Test
    void bedtimesEitherSideOfMidnightAreConsistent() {
        ConsistencyReport report = ConsistencyReport.of(List.of(
                night().bedtime("23:50").log(), night().bedtime("00:10").log(), night().bedtime("23:55").log()))
                .orElseThrow();
        assertEquals(100, report.bedtime());
    }

    @Test
    void erraticScheduleScoresLow() {
        ConsistencyReport report = ConsistencyReport.of(List.of(
                night().bedtime("21:00").wakeTime("05:00").sleepHours(8).log(),
                night().bedtime("03:00").wakeTime("11:00").sleepHours(4).log(),
                night().bedtime("22:00").wakeTime("09:30").sleepHours(11).log()))
                .orElseThrow();
        assertEquals(0, report.bedtime());
        assertTrue(report.overall() < 20);
        assertEquals("Very poor", report.rating());
    }

    @Test
    void scoreMapsVarianceLinearly() {
        assertEquals(100, ConsistencyReport.score(0.1, 0.25, 4.0));
        assertEquals(50, ConsistencyReport.score(2.125, 0.25, 4.0));
        assertEquals(0, ConsistencyReport.score(5, 0.25, 4.0));
    }
}
