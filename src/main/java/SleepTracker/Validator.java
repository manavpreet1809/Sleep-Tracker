package SleepTracker;

public interface Validator {

    /**
     * Error trapping function which repeatedly prompts user until they enter a valid input.
     *
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return Validated input.
     */
    double validateStream(double min, double max);

    boolean validateVariable(Object o, double min, double max);
}
