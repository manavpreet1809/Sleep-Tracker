package io.github.manavpreet1809.sleeptracker.api;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import io.github.manavpreet1809.sleeptracker.nights.SleepNight;
import java.time.LocalDate;
import java.time.LocalTime;

public record NightResponse(
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
        String source) {

    static NightResponse of(SleepNight stored) {
        SleepLog n = stored.toLog();
        return new NightResponse(n.date(), n.quality(), n.bedtime(), n.wakeTime(), n.sleepHours(), n.roomTemperatureC(),
                n.cardioMinutes(), n.napMinutes(), n.screenTimeHours(), n.mattressAgeMonths(), n.waterIntakeMl(),
                stored.source().label());
    }
}
