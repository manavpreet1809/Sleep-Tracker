package io.github.manavpreet1809.sleeptracker.api;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.EnumMap;
import java.util.Map;

/** Tonight's plans. Anything left out is assumed to be typical. */
public record PredictionRequest(
        @DecimalMin("0") @DecimalMax("24") Double sleepHours,
        @DecimalMin("-10") @DecimalMax("45") Double roomTemperatureC,
        @Min(0) @Max(1440) Integer cardioMinutes,
        @Min(0) @Max(1440) Integer napMinutes,
        @DecimalMin("0") @DecimalMax("24") Double screenTimeHours,
        @Min(0) @Max(20000) Integer waterIntakeMl) {

    Map<SleepFactor, Double> toFactors() {
        Map<SleepFactor, Double> factors = new EnumMap<>(SleepFactor.class);
        put(factors, SleepFactor.SLEEP_DURATION, sleepHours);
        put(factors, SleepFactor.ROOM_TEMPERATURE, roomTemperatureC);
        put(factors, SleepFactor.CARDIO, cardioMinutes);
        put(factors, SleepFactor.NAPPING, napMinutes);
        put(factors, SleepFactor.SCREEN_TIME, screenTimeHours);
        put(factors, SleepFactor.WATER_INTAKE, waterIntakeMl);
        return factors;
    }

    private static void put(Map<SleepFactor, Double> factors, SleepFactor factor, Number value) {
        if (value != null) {
            factors.put(factor, value.doubleValue());
        }
    }
}
