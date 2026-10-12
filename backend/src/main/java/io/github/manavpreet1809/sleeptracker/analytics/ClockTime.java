package io.github.manavpreet1809.sleeptracker.analytics;

import java.time.LocalTime;
import java.util.List;

/**
 * Statistics for times of day, which wrap around midnight: the average of 23:45 and 00:03 is
 * 23:54, not midday. Each time is treated as an angle on a 24-hour clock face.
 */
public final class ClockTime {

    private static final double HOURS_PER_DAY = 24;

    private ClockTime() {
    }

    public static double toHours(LocalTime time) {
        return time.toSecondOfDay() / 3600.0;
    }

    public static LocalTime fromHours(double hours) {
        double wrapped = ((hours % HOURS_PER_DAY) + HOURS_PER_DAY) % HOURS_PER_DAY;
        long minutes = Math.round(wrapped * 60) % (24 * 60);
        return LocalTime.ofSecondOfDay(minutes * 60);
    }

    /** Circular mean in hours since midnight, from 0 (inclusive) to 24 (exclusive). */
    public static double meanHours(List<LocalTime> times) {
        if (times.isEmpty()) {
            throw new IllegalArgumentException("At least one time is required");
        }
        double x = 0;
        double y = 0;
        for (LocalTime time : times) {
            double angle = toHours(time) / HOURS_PER_DAY * 2 * Math.PI;
            x += Math.cos(angle);
            y += Math.sin(angle);
        }
        double hours = Math.atan2(y, x) / (2 * Math.PI) * HOURS_PER_DAY;
        return hours < 0 ? hours + HOURS_PER_DAY : hours;
    }

    public static LocalTime mean(List<LocalTime> times) {
        return fromHours(meanHours(times));
    }

    /**
     * Sample variance in hours², measuring each time's shortest distance around the clock from the
     * circular mean, so 23:30 and 00:30 are one hour apart rather than twenty-three.
     */
    public static double sampleVarianceHours(List<LocalTime> times) {
        if (times.size() < 2) {
            return 0;
        }
        double mean = meanHours(times);
        double sum = 0;
        for (LocalTime time : times) {
            double difference = toHours(time) - mean;
            difference -= HOURS_PER_DAY * Math.round(difference / HOURS_PER_DAY); // into [-12, 12]
            sum += difference * difference;
        }
        return sum / (times.size() - 1);
    }
}
