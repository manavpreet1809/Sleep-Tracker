package io.github.manavpreet1809.sleeptracker.analytics;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.util.List;
import java.util.Optional;

/**
 * How regular someone's sleep schedule is. Each score runs from 0 (erratic) to 100 (very regular).
 *
 * @param overall          weighted score: 40% bedtime, 40% wake time, 20% duration
 * @param bedtime          bedtime regularity
 * @param wakeTime         wake-time regularity
 * @param duration         sleep-duration regularity
 * @param bedtimeVariance  sample variance of bedtime, in hours²
 * @param wakeTimeVariance sample variance of wake time, in hours²
 * @param durationVariance sample variance of sleep duration, in hours²
 */
public record ConsistencyReport(
        int overall,
        int bedtime,
        int wakeTime,
        int duration,
        double bedtimeVariance,
        double wakeTimeVariance,
        double durationVariance) {

    /** Fewer nights than this give no meaningful variance. */
    public static final int MINIMUM_NIGHTS = 3;

    public static Optional<ConsistencyReport> of(List<SleepLog> logs) {
        if (logs.size() < MINIMUM_NIGHTS) {
            return Optional.empty();
        }
        double bedtimeVariance = ClockTime.sampleVarianceHours(logs.stream().map(SleepLog::bedtime).toList());
        double wakeTimeVariance = ClockTime.sampleVarianceHours(logs.stream().map(SleepLog::wakeTime).toList());
        double durationVariance = Statistics.sampleVariance(SleepFactor.SLEEP_DURATION.of(logs));

        // A spread under ~30 minutes is excellent; over ~2 hours is poor.
        int bedtime = score(bedtimeVariance, 0.25, 4.0);
        int wakeTime = score(wakeTimeVariance, 0.25, 4.0);
        int duration = score(durationVariance, 0.5, 6.0);
        int overall = (int) (bedtime * 0.4 + wakeTime * 0.4 + duration * 0.2);
        return Optional.of(new ConsistencyReport(overall, bedtime, wakeTime, duration,
                bedtimeVariance, wakeTimeVariance, durationVariance));
    }

    /** Maps variance linearly onto 100 (at or below {@code excellent}) down to 0 (at or above {@code poor}). */
    static int score(double variance, double excellent, double poor) {
        if (variance <= excellent) {
            return 100;
        }
        if (variance >= poor) {
            return 0;
        }
        return 100 - (int) ((variance - excellent) / (poor - excellent) * 100);
    }

    public String rating() {
        if (overall >= 90) return "Excellent";
        if (overall >= 75) return "Very good";
        if (overall >= 60) return "Good";
        if (overall >= 40) return "Fair";
        if (overall >= 20) return "Poor";
        return "Very poor";
    }
}
