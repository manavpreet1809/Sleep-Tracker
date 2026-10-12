package io.github.manavpreet1809.sleeptracker.analytics;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.util.function.Predicate;

/** A yes/no habit commonly linked to better sleep, using the thresholds from the original app. */
public enum Habit {
    CARDIO("60+ minutes of cardio", night -> night.cardioMinutes() >= 60),
    COOL_ROOM("Bedroom at or below 18.5 °C", night -> night.roomTemperatureC() <= 18.5),
    LIMITED_SCREENS("4 hours or less of screen time", night -> night.screenTimeHours() <= 4),
    HYDRATED("2 L or more of water", night -> night.waterIntakeMl() >= 2000),
    SHORT_NAPS("Naps of 30 minutes or less", night -> night.napMinutes() <= 30),
    FULL_NIGHT("8+ hours of sleep", night -> night.sleepHours() >= 8);

    private final String description;
    private final Predicate<SleepLog> test;

    Habit(String description, Predicate<SleepLog> test) {
        this.description = description;
        this.test = test;
    }

    public String description() {
        return description;
    }

    public boolean followedOn(SleepLog night) {
        return test.test(night);
    }
}
