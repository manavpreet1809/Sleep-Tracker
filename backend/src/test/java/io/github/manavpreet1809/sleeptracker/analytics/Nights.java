package io.github.manavpreet1809.sleeptracker.analytics;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.time.LocalDate;
import java.time.LocalTime;

/** Test nights with sensible defaults, so each test states only what it is about. */
record Nights(LocalDate date, int quality, LocalTime bedtime, LocalTime wakeTime, double sleepHours,
              Double roomTemperatureC, Integer cardioMinutes, Integer napMinutes, Double screenTimeHours,
              Integer mattressAgeMonths, Integer waterIntakeMl) {

    static Nights night() {
        return new Nights(LocalDate.of(2026, 1, 1), 6, LocalTime.of(23, 0), LocalTime.of(7, 0), 8,
                19.0, 30, 0, 3.0, 24, 2000);
    }

    Nights quality(int value) { return new Nights(date, value, bedtime, wakeTime, sleepHours, roomTemperatureC, cardioMinutes, napMinutes, screenTimeHours, mattressAgeMonths, waterIntakeMl); }
    Nights bedtime(String value) { return new Nights(date, quality, LocalTime.parse(value), wakeTime, sleepHours, roomTemperatureC, cardioMinutes, napMinutes, screenTimeHours, mattressAgeMonths, waterIntakeMl); }
    Nights wakeTime(String value) { return new Nights(date, quality, bedtime, LocalTime.parse(value), sleepHours, roomTemperatureC, cardioMinutes, napMinutes, screenTimeHours, mattressAgeMonths, waterIntakeMl); }
    Nights sleepHours(double value) { return new Nights(date, quality, bedtime, wakeTime, value, roomTemperatureC, cardioMinutes, napMinutes, screenTimeHours, mattressAgeMonths, waterIntakeMl); }
    Nights cardio(Integer value) { return new Nights(date, quality, bedtime, wakeTime, sleepHours, roomTemperatureC, value, napMinutes, screenTimeHours, mattressAgeMonths, waterIntakeMl); }
    Nights screenTime(Double value) { return new Nights(date, quality, bedtime, wakeTime, sleepHours, roomTemperatureC, cardioMinutes, napMinutes, value, mattressAgeMonths, waterIntakeMl); }
    Nights water(Integer value) { return new Nights(date, quality, bedtime, wakeTime, sleepHours, roomTemperatureC, cardioMinutes, napMinutes, screenTimeHours, mattressAgeMonths, value); }

    SleepLog log() {
        return new SleepLog(date, quality, bedtime, wakeTime, sleepHours, roomTemperatureC, cardioMinutes,
                napMinutes, screenTimeHours, mattressAgeMonths, waterIntakeMl);
    }
}
