package io.github.manavpreet1809.sleeptracker.analytics;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.util.function.DoublePredicate;

/** A yes/no habit commonly linked to better sleep, using the thresholds from the original app. */
public enum Habit {
    CARDIO("60+ minutes of cardio", SleepFactor.CARDIO, minutes -> minutes >= 60),
    COOL_ROOM("Bedroom at or below 18.5 °C", SleepFactor.ROOM_TEMPERATURE, celsius -> celsius <= 18.5),
    LIMITED_SCREENS("4 hours or less of screen time", SleepFactor.SCREEN_TIME, hours -> hours <= 4),
    HYDRATED("2 L or more of water", SleepFactor.WATER_INTAKE, millilitres -> millilitres >= 2000),
    SHORT_NAPS("Naps of 30 minutes or less", SleepFactor.NAPPING, minutes -> minutes <= 30),
    FULL_NIGHT("8+ hours of sleep", SleepFactor.SLEEP_DURATION, hours -> hours >= 8);

    private final String description;
    private final SleepFactor factor;
    private final DoublePredicate test;

    Habit(String description, SleepFactor factor, DoublePredicate test) {
        this.description = description;
        this.factor = factor;
        this.test = test;
    }

    public String description() {
        return description;
    }

    /** Whether the habit can be judged on this night, i.e. its measurement was recorded. */
    public boolean recordedOn(SleepLog night) {
        return factor.recordedOn(night);
    }

    /** Whether the habit was followed; only meaningful when {@link #recordedOn} is true. */
    public boolean followedOn(SleepLog night) {
        Double value = factor.of(night);
        return value != null && test.test(value);
    }
}
