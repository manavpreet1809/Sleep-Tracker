package SleepTracker;

/**
 * Enum representing different sleep-related factors that can be tracked and analyzed.
 */
public enum Factor {
    SLEEP_QUALITY("Sleep Quality", "1-10 scale"),
    ROOM_TEMPERATURE("Room Temperature", "°C"),
    SLEEP_DURATION("Sleep Duration", "hours"),
    CARDIO_DURATION("Cardio Duration", "minutes"),
    NAPPING_DURATION("Napping Duration", "minutes"),
    SCREEN_TIME("Screen Time", "hours"),
    MATTRESS_AGE("Mattress Age", "months"),
    WATER_INTAKE("Water Intake", "mL"),
    SLEEP_TIME("Sleep Time", "24hr"),
    WAKE_TIME("Wake Time", "24hr");

    private final String displayName;
    private final String measurementUnit;

    Factor(String displayName, String measurementUnit) {
        this.displayName = displayName;
        this.measurementUnit = measurementUnit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    /**
     * Returns the corresponding index position for this factor in data arrays.
     * Matches the 1-based indexing used in the original implementation.
     */
    public int getArrayIndex() {
        return this.ordinal() + 1;
    }

    @Override
    public String toString() {
        return displayName + " (" + measurementUnit + ")";
    }
}
