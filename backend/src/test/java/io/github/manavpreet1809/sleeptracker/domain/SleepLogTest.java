package io.github.manavpreet1809.sleeptracker.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class SleepLogTest {

    private static SleepLog log(int quality, double sleepHours) {
        return new SleepLog(LocalDate.of(2026, 1, 1), quality, LocalTime.of(23, 0), LocalTime.of(7, 0),
                sleepHours, 19.0, 30, 0, 3.0, 24, 2000);
    }

    @Test
    void acceptsValidNight() {
        assertDoesNotThrow(() -> log(7, 8));
    }

    @Test
    void rejectsOutOfRangeValues() {
        assertThrows(IllegalArgumentException.class, () -> log(0, 8));
        assertThrows(IllegalArgumentException.class, () -> log(11, 8));
        assertThrows(IllegalArgumentException.class, () -> log(7, 25));
        assertThrows(IllegalArgumentException.class, () -> log(7, Double.NaN));
    }

    @Test
    void rejectsMissingTimes() {
        assertThrows(NullPointerException.class, () -> new SleepLog(LocalDate.of(2026, 1, 1), 7, null,
                LocalTime.of(7, 0), 8, 19.0, 30, 0, 3.0, 24, 2000));
    }
}
