package io.github.manavpreet1809.sleeptracker.api;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/** A night as entered by the user. Habits may be left out. */
public record NightRequest(
        @NotNull @Schema(example = "2026-10-11") LocalDate date,
        @NotNull @Min(1) @Max(10) @Schema(description = "Self-rated, 1 to 10", example = "7") Integer quality,
        @NotNull @Schema(type = "string", example = "23:15") LocalTime bedtime,
        @NotNull @Schema(type = "string", example = "07:00") LocalTime wakeTime,
        @NotNull @DecimalMin("0") @DecimalMax("24") @Schema(example = "7.5") Double sleepHours,
        @DecimalMin("-10") @DecimalMax("45") @Schema(example = "19") Double roomTemperatureC,
        @Min(0) @Max(1440) @Schema(example = "45") Integer cardioMinutes,
        @Min(0) @Max(1440) @Schema(example = "0") Integer napMinutes,
        @DecimalMin("0") @DecimalMax("24") @Schema(example = "3") Double screenTimeHours,
        @Min(0) @Max(600) @Schema(example = "24") Integer mattressAgeMonths,
        @Min(0) @Max(20000) @Schema(example = "2000") Integer waterIntakeMl) {

    SleepLog toLog() {
        return new SleepLog(date, quality, bedtime, wakeTime, sleepHours, roomTemperatureC, cardioMinutes, napMinutes,
                screenTimeHours, mattressAgeMonths, waterIntakeMl);
    }
}
