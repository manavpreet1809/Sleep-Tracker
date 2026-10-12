package io.github.manavpreet1809.sleeptracker.analytics;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;

/**
 * A rough estimate of sleep structure from duration and self-rated quality. Without a wearable's
 * sensor data this is an approximation for illustration, not a measurement.
 *
 * @param cycles             complete sleep cycles
 * @param cycleMinutes       assumed length of one cycle
 * @param remMinutes         estimated REM sleep
 * @param deepSleepMinutes   estimated deep sleep
 */
public record SleepCycleEstimate(int cycles, double cycleMinutes, double remMinutes, double deepSleepMinutes) {

    public static SleepCycleEstimate of(SleepLog night) {
        double hours = night.sleepHours();
        int quality = night.quality();
        // A typical cycle is about 90 minutes; restful nights run slightly shorter cycles.
        double cycleHours = quality >= 7 ? 1.4 : quality <= 3 ? 1.7 : 1.5;
        double remShare = 0.2 + quality / 50.0;         // 22% to 40% of the night
        double deepShare = 0.1 + quality / 100.0;       // 11% to 20% of the night
        return new SleepCycleEstimate((int) (hours / cycleHours), cycleHours * 60,
                hours * remShare * 60, hours * deepShare * 60);
    }
}
