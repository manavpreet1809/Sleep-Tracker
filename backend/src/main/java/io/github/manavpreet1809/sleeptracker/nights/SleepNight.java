package io.github.manavpreet1809.sleeptracker.nights;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** A stored night in one workspace. */
@Entity
@Table(name = "sleep_night")
public class SleepNight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(nullable = false, updatable = false)
    private LocalDate nightDate;

    private int quality;
    private LocalTime bedtime;
    private LocalTime wakeTime;
    private double sleepHours;
    @Column(name = "room_temperature_c")
    private Double roomTemperatureC;
    private Integer cardioMinutes;
    private Integer napMinutes;
    private Double screenTimeHours;
    private Integer mattressAgeMonths;
    private Integer waterIntakeMl;
    private Integer sleepScore;
    private Integer restingHeartRate;
    private Integer hrvMs;

    @Column(nullable = false)
    private String source;

    private Instant createdAt;
    private Instant updatedAt;

    protected SleepNight() {
        // for JPA
    }

    public SleepNight(UUID workspaceId, SleepLog night, NightSource source) {
        this.workspaceId = workspaceId;
        this.nightDate = night.date();
        apply(night, source);
    }

    /** Replaces everything recorded about the night; the date and workspace never change. */
    public void apply(SleepLog night, NightSource source) {
        if (!night.date().equals(nightDate)) {
            throw new IllegalArgumentException("A night's date cannot change");
        }
        this.quality = night.quality();
        this.bedtime = night.bedtime();
        this.wakeTime = night.wakeTime();
        this.sleepHours = night.sleepHours();
        this.roomTemperatureC = night.roomTemperatureC();
        this.cardioMinutes = night.cardioMinutes();
        this.napMinutes = night.napMinutes();
        this.screenTimeHours = night.screenTimeHours();
        this.mattressAgeMonths = night.mattressAgeMonths();
        this.waterIntakeMl = night.waterIntakeMl();
        this.sleepScore = night.sleepScore();
        this.restingHeartRate = night.restingHeartRate();
        this.hrvMs = night.hrvMs();
        this.source = source.label();
    }

    public SleepLog toLog() {
        return new SleepLog(nightDate, quality, bedtime, wakeTime, sleepHours, roomTemperatureC, cardioMinutes,
                napMinutes, screenTimeHours, mattressAgeMonths, waterIntakeMl, sleepScore, restingHeartRate, hrvMs);
    }

    public LocalDate nightDate() {
        return nightDate;
    }

    public NightSource source() {
        return NightSource.fromLabel(source);
    }

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
