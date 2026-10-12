package io.github.manavpreet1809.sleeptracker.nights;

import java.time.LocalDate;

public class NightNotFoundException extends RuntimeException {

    public NightNotFoundException(LocalDate date) {
        super("No night recorded for " + date);
    }
}
