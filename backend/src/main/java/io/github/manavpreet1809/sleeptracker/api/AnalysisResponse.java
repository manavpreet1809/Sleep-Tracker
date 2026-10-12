package io.github.manavpreet1809.sleeptracker.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Everything the dashboard shows. Sections that need more nights than recorded are null.
 *
 * @param nights        nights recorded
 * @param summary       averages; null without nights
 * @param consistency   schedule regularity; null with fewer than three nights
 * @param qualityHabits how each habit relates to sleep quality, largest difference first
 * @param durationHabits how each habit relates to sleep duration
 * @param lastNight     estimated sleep cycles for the most recent night; null without nights
 * @param model         the quality model's view of each habit; null until it has enough nights
 * @param trend         one point per night, for charts
 */
public record AnalysisResponse(
        int nights,
        Summary summary,
        Consistency consistency,
        List<HabitResult> qualityHabits,
        List<HabitResult> durationHabits,
        Cycles lastNight,
        Model model,
        List<TrendPoint> trend) {

    public record Factor(String key, String name, String unit, double value) {
    }

    public record Summary(List<Factor> averages, LocalTime bedtime, LocalTime wakeTime) {
    }

    public record Consistency(int overall, int bedtime, int wakeTime, int duration, String rating) {
    }

    public record HabitResult(String key, String description, int nightsWith, int nightsWithout,
                              Double averageWith, Double averageWithout, Double difference) {
    }

    public record Cycles(LocalDate date, int cycles, double cycleMinutes, double remMinutes, double deepSleepMinutes) {
    }

    /** {@code effects} are the change in predicted quality per one-standard-deviation increase. */
    public record Model(int nights, List<Factor> effects) {
    }

    /** Smoothed values weight the latest seven nights, newest most. */
    public record TrendPoint(LocalDate date, int quality, double sleepHours, LocalTime bedtime, LocalTime wakeTime,
                             double qualityTrend, double sleepHoursTrend) {
    }
}
