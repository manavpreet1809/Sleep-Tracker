package io.github.manavpreet1809.sleeptracker.domain;

import java.util.List;
import java.util.function.ToDoubleFunction;

/** The numeric measurements of a {@link SleepLog} that can be averaged, compared, and modelled. */
public enum SleepFactor {
    QUALITY("Sleep quality", "1-10", SleepLog::quality),
    SLEEP_DURATION("Sleep duration", "hours", SleepLog::sleepHours),
    ROOM_TEMPERATURE("Room temperature", "°C", SleepLog::roomTemperatureC),
    CARDIO("Cardio", "minutes", SleepLog::cardioMinutes),
    NAPPING("Napping", "minutes", SleepLog::napMinutes),
    SCREEN_TIME("Screen time", "hours", SleepLog::screenTimeHours),
    MATTRESS_AGE("Mattress age", "months", SleepLog::mattressAgeMonths),
    WATER_INTAKE("Water intake", "mL", SleepLog::waterIntakeMl);

    private final String displayName;
    private final String unit;
    private final ToDoubleFunction<SleepLog> accessor;

    SleepFactor(String displayName, String unit, ToDoubleFunction<SleepLog> accessor) {
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

    public double of(SleepLog log) {
        return accessor.applyAsDouble(log);
    }

    public double[] of(List<SleepLog> logs) {
        return logs.stream().mapToDouble(accessor).toArray();
    }
}
