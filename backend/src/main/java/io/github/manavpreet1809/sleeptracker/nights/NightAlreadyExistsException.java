package io.github.manavpreet1809.sleeptracker.nights;

import java.time.LocalDate;

public class NightAlreadyExistsException extends RuntimeException {

    public NightAlreadyExistsException(LocalDate date) {
        super("A night is already recorded for " + date + "; update it instead");
    }
}
