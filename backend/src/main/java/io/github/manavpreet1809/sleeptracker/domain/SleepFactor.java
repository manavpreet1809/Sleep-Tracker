package io.github.manavpreet1809.sleeptracker.domain;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * The numeric measurements of a {@link SleepLog} that can be averaged, compared, and modelled.
 * Optional habits read as null on nights they were not recorded.
 */
public enum SleepFactor {
    QUALITY("Sleep quality", "1-10", night -> (double) night.quality()),
    SLEEP_DURATION("Sleep duration", "hours", SleepLog::sleepHours),
    ROOM_TEMPERATURE("Room temperature", "°C", SleepLog::roomTemperatureC),
    CARDIO("Cardio", "minutes", SleepLog::cardioMinutes),
    NAPPING("Napping", "minutes", SleepLog::napMinutes),
    SCREEN_TIME("Screen time", "hours", SleepLog::screenTimeHours),
    MATTRESS_AGE("Mattress age", "months", SleepLog::mattressAgeMonths),
    WATER_INTAKE("Water intake", "mL", SleepLog::waterIntakeMl);

    private final String displayName;
    private final String unit;
    private final Function<SleepLog, ? extends Number> accessor;

    SleepFactor(String displayName, String unit, Function<SleepLog, ? extends Number> accessor) {
        this.displayName = displayName;
        this.unit = unit;
        this.accessor = accessor;
    }

    public String displayName() {
        return displayName;
    }

    public String unit() {
        return unit;
    }

    /** The value on one night, or null if it was not recorded. */
    public Double of(SleepLog night) {
        Number value = accessor.apply(night);
        return value == null ? null : value.doubleValue();
    }

    public boolean recordedOn(SleepLog night) {
        return accessor.apply(night) != null;
    }

    /** Values from the nights where this factor was recorded, in order. */
    public double[] of(List<SleepLog> nights) {
        return nights.stream().map(this::of).filter(Objects::nonNull).mapToDouble(Double::doubleValue).toArray();
    }
}
