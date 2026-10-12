package io.github.manavpreet1809.sleeptracker.analytics;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Averages across a set of nights. Bedtime and wake time use the clock-aware mean.
 *
 * @param nights   number of nights summarised
 * @param averages mean of every numeric factor
 * @param bedtime  typical bedtime
 * @param wakeTime typical wake time
 */
public record SleepSummary(int nights, Map<SleepFactor, Double> averages, LocalTime bedtime, LocalTime wakeTime) {

    public SleepSummary {
        averages = Map.copyOf(averages);
    }

    public static SleepSummary of(List<SleepLog> logs) {
        if (logs.isEmpty()) {
            throw new IllegalArgumentException("At least one night is required");
        }
        Map<SleepFactor, Double> averages = new EnumMap<>(SleepFactor.class);
        for (SleepFactor factor : SleepFactor.values()) {
            averages.put(factor, Statistics.mean(factor.of(logs)));
        }
        return new SleepSummary(logs.size(), averages,
                ClockTime.mean(logs.stream().map(SleepLog::bedtime).toList()),
                ClockTime.mean(logs.stream().map(SleepLog::wakeTime).toList()));
    }

    public double average(SleepFactor factor) {
        return averages.get(factor);
    }
}
