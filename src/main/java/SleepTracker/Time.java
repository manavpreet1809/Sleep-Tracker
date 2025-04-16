package SleepTracker;

public class Time implements Comparable<Time>{
    double timeNum;
    String timeString;

    public String getTimeString() {
        return this.timeString;
    }

    public double getTimeNum() {
        return this.timeNum;
    }


    public Time(double timeNum) {
        this.timeNum = timeNum;
        timeString = showTime(timeNum);
    }

    public Time(String timeString) {
        this.timeString = timeString;
        this.timeNum = toTimeNum(timeString);
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

    public double toTimeNum(String strTime) {
        double res = 0;
        res += Integer.parseInt(strTime.substring(0, 2));
        res += Double.parseDouble(strTime.substring(3, 5)) / 60;
        return res;
    }

    @Override
    public int compareTo(Time other) {
        return Double.compare(this.timeNum, other.getTimeNum());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true; // pointer equality
        }
        if (!(obj instanceof Time)) { // pattern variable
            return false; // wrong type
        }

        return this.getTimeNum() == ((Time) obj).getTimeNum();
    }

    @Override
    public String toString() {
        return this.timeString;
    }
}
