package io.github.manavpreet1809.sleeptracker.demo;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Made-up but plausible nights for trying the app: screens and a warm room hurt sleep, cardio and
 * a full night help, and weekends run later. The same seed always gives the same nights.
 */
public final class DemoNights {

    private DemoNights() {
    }

    public static List<SleepLog> generate(LocalDate lastNight, int count, long seed) {
        Random random = new Random(seed);
        List<SleepLog> nights = new ArrayList<>();
        for (int i = count - 1; i >= 0; i--) {
            LocalDate date = lastNight.minusDays(i);
            boolean weekend = date.getDayOfWeek().getValue() >= 5; // Friday and Saturday nights
            double screen = clamp(round(2.5 + random.nextGaussian() * 1.5 + (weekend ? 1.5 : 0), 0.5), 0, 12);
            int cardio = random.nextDouble() < 0.45 ? 30 + 15 * random.nextInt(5) : 0;
            double room = round(18.5 + random.nextGaussian() * 1.5, 0.5);
            int water = (int) clamp(Math.round((1900 + random.nextGaussian() * 450) / 50) * 50, 500, 4000);
            int nap = random.nextDouble() < 0.2 ? 15 + 15 * random.nextInt(4) : 0;

            // Later bedtimes with more screens and on weekends; minutes after 21:00.
            double bedMinutes = 120 + screen * 12 + (weekend ? 50 : 0) + random.nextGaussian() * 25;
            LocalTime bedtime = LocalTime.of(21, 0).plusMinutes(Math.round(bedMinutes / 5) * 5);
            double asleepHours = clamp(round(7.6 - screen * 0.12 - nap / 60.0 * 0.4
                    + (weekend ? 0.8 : 0) + random.nextGaussian() * 0.6, 0.25), 4, 10.5);
            LocalTime wake = bedtime.plusMinutes(Math.round((asleepHours * 60 + 15) / 5) * 5);

            double quality = 5.2 + (asleepHours - 7) * 0.8 - (screen - 2.5) * 0.45
                    + (cardio > 0 ? 0.7 : 0) - Math.max(0, room - 19.5) * 0.5 + random.nextGaussian() * 0.8;
            nights.add(new SleepLog(date, (int) clamp(Math.round(quality), 1, 10), bedtime, wake, asleepHours,
                    room, cardio, nap, screen, 30, water));
        }
        return nights;
    }

    private static double round(double value, double step) {
        return Math.round(value / step) * step;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
