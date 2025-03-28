public class Time {
    double timeNum;
    private Time t2;

    public String getTimeString() {
        return timeString;
    }

    String timeString;

    public Time(double timeNum) {
        this.timeNum = timeNum;
        timeString = showTime(timeNum);
    }

    /**
     * Takes a number representing time in "decimal format" and returns a string with the time in
     * classic 24 hr format.
     * @param timeNum decimal number representing a time (used internally)
     * @return time in 24 hr format
     */
    public String showTime(double timeNum) {
        String res = "";
        res += String.format("%02d", (int) timeNum);
        res += ":";
        int minutes = (int) ((timeNum - ((int) timeNum)) * 60);
        res += String.format("%02d", minutes);
        return res;
    }

    public int compareTo(Time t2) {
        this.t2 = t2;
    }
}
