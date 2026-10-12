package io.github.manavpreet1809.sleeptracker.nights;

import java.util.Arrays;

/** Where a night's record came from. */
public enum NightSource {
    MANUAL("manual"),
    DEMO("demo");

    private final String label;

    NightSource(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    static NightSource fromLabel(String label) {
        return Arrays.stream(values()).filter(s -> s.label.equals(label)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown night source " + label));
    }
}
