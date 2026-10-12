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
 *
 * <p>The remaining habits are optional (null when not recorded):
 *
 * @param roomTemperatureC  bedroom temperature in degrees Celsius
 * @param cardioMinutes     cardio exercise that day
 * @param napMinutes        daytime napping
 * @param screenTimeHours   screen time that day
 * @param mattressAgeMonths age of the mattress
 * @param waterIntakeMl     water drunk that day
 * @param sleepScore        a wearable's 0-100 sleep score
 * @param restingHeartRate  overnight resting heart rate from a wearable, beats per minute
 * @param hrvMs             overnight heart-rate variability from a wearable, milliseconds
 */
public record SleepLog(
        LocalDate date,
        int quality,
        LocalTime bedtime,
        LocalTime wakeTime,
        double sleepHours,
        Double roomTemperatureC,
        Integer cardioMinutes,
        Integer napMinutes,
        Double screenTimeHours,
        Integer mattressAgeMonths,
        Integer waterIntakeMl,
        Integer sleepScore,
        Integer restingHeartRate,
        Integer hrvMs) {

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
        requireRange("sleepScore", sleepScore, 0, 100);
        requireRange("restingHeartRate", restingHeartRate, 20, 220);
        requireRange("hrvMs", hrvMs, 1, 500);
    }

    /** A night logged by hand, without wearable measurements. */
    public SleepLog(LocalDate date, int quality, LocalTime bedtime, LocalTime wakeTime, double sleepHours,
                    Double roomTemperatureC, Integer cardioMinutes, Integer napMinutes, Double screenTimeHours,
                    Integer mattressAgeMonths, Integer waterIntakeMl) {
        this(date, quality, bedtime, wakeTime, sleepHours, roomTemperatureC, cardioMinutes, napMinutes,
                screenTimeHours, mattressAgeMonths, waterIntakeMl, null, null, null);
    }

    /** This night with any wearable measurements it lacks taken from {@code earlier}. */
    public SleepLog keepingMeasurementsFrom(SleepLog earlier) {
        return new SleepLog(date, quality, bedtime, wakeTime, sleepHours, roomTemperatureC, cardioMinutes, napMinutes,
                screenTimeHours, mattressAgeMonths, waterIntakeMl,
                sleepScore != null ? sleepScore : earlier.sleepScore,
                restingHeartRate != null ? restingHeartRate : earlier.restingHeartRate,
                hrvMs != null ? hrvMs : earlier.hrvMs);
    }

    /** This night's sleep measurements with the habits recorded on {@code habits}. */
    public SleepLog withHabitsFrom(SleepLog habits) {
        return new SleepLog(date, quality, bedtime, wakeTime, sleepHours, habits.roomTemperatureC,
                habits.cardioMinutes, habits.napMinutes, habits.screenTimeHours, habits.mattressAgeMonths,
                habits.waterIntakeMl, sleepScore, restingHeartRate, hrvMs);
    }

    private static void requireRange(String name, Number value, double min, double max) {
        if (value == null) {
            return; // optional habit not recorded
        }
        double v = value.doubleValue();
        if (!(v >= min && v <= max)) { // also rejects NaN
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max + ", was " + v);
        }
    }
}
