package io.github.manavpreet1809.sleeptracker.analytics;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Compares an outcome, such as sleep quality, on nights with and without a habit. A difference
 * shows association in the recorded nights, not that the habit caused it.
 *
 * @param habit          the habit compared
 * @param outcome        the measurement compared
 * @param nightsWith     nights the habit was followed
 * @param nightsWithout  nights it was not
 * @param averageWith    mean outcome with the habit, or null without such nights
 * @param averageWithout mean outcome without the habit, or null without such nights
 */
public record HabitImpact(
        Habit habit,
        SleepFactor outcome,
        int nightsWith,
        int nightsWithout,
        Double averageWith,
        Double averageWithout) {

    /** How much higher the outcome was with the habit; null unless both groups have nights. */
    public Double difference() {
        return averageWith == null || averageWithout == null ? null : averageWith - averageWithout;
    }

    /** Every habit's impact on {@code outcome}, largest difference first and incomparable habits last. */
    public static List<HabitImpact> of(List<SleepLog> logs, SleepFactor outcome) {
        return Arrays.stream(Habit.values())
                // "8+ hours of sleep" trivially predicts sleep duration.
                .filter(habit -> !(habit == Habit.FULL_NIGHT && outcome == SleepFactor.SLEEP_DURATION))
                .map(habit -> compare(logs, habit, outcome))
                .sorted(Comparator.comparing((HabitImpact impact) -> impact.difference() == null)
                        .thenComparing(impact -> -Math.abs(impact.difference() == null ? 0 : impact.difference())))
                .toList();
    }

    private static HabitImpact compare(List<SleepLog> logs, Habit habit, SleepFactor outcome) {
        List<SleepLog> with = logs.stream().filter(habit::followedOn).toList();
        List<SleepLog> without = logs.stream().filter(night -> !habit.followedOn(night)).toList();
        return new HabitImpact(habit, outcome, with.size(), without.size(), mean(with, outcome), mean(without, outcome));
    }

    private static Double mean(List<SleepLog> nights, SleepFactor outcome) {
        return nights.isEmpty() ? null : Statistics.mean(outcome.of(nights));
    }
}
