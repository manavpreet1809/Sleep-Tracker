public interface Validator {
    /**
     * Error trapping function which repeatedly prompts user until they enter a valid input.
     *
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return Validated input.
     */
    double validate(double min, double max) throws Exception;

}
