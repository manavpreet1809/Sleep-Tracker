package io.github.manavpreet1809.sleeptracker.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * One night of sleep and the habits recorded around it.
 *
 * @param date              the date the night started
 * @param quality           self-rated sleep quality, 1 (worst) to 10 (best)
 * @param bedtime           when the person fell asleep
 * @param wakeTime          when the person woke up
 * @param sleepHours        time actually asleep, which can be shorter than bedtime to wake time
 * @param roomTemperatureC  bedroom temperature in degrees Celsius
 * @param cardioMinutes     cardio exercise that day
 * @param napMinutes        daytime napping
 * @param screenTimeHours   screen time that day
 * @param mattressAgeMonths age of the mattress
 * @param waterIntakeMl     water drunk that day
 */
public record SleepLog(
        LocalDate date,
        int quality,
        LocalTime bedtime,
        LocalTime wakeTime,
        double sleepHours,
        double roomTemperatureC,
        int cardioMinutes,
        int napMinutes,
        double screenTimeHours,
        int mattressAgeMonths,
        int waterIntakeMl) {

    public SleepLog {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(bedtime, "bedtime");
        Objects.requireNonNull(wakeTime, "wakeTime");
        requireRange("quality", quality, 1, 10);
        requireRange("sleepHours", sleepHours, 0, 24);
        requireRange("roomTemperatureC", roomTemperatureC, -10, 45);
        requireRange("cardioMinutes", cardioMinutes, 0, 24 * 60);
        requireRange("napMinutes", napMinutes, 0, 24 * 60);
        requireRange("screenTimeHours", screenTimeHours, 0, 24);
        requireRange("mattressAgeMonths", mattressAgeMonths, 0, 600);
        requireRange("waterIntakeMl", waterIntakeMl, 0, 20_000);
    }

    private static void requireRange(String name, double value, double min, double max) {
        if (!(value >= min && value <= max)) { // also rejects NaN
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max + ", was " + value);
        }
    }
}
