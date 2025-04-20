package SleepTracker;

import java.util.ArrayList;

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
        int minutes = (int) Math.round((timeNum - ((int) timeNum)) * 60);
        res += String.format("%02d", minutes);
        return res;
    }

    public double toTimeNum(String strTime) {
        int hours = Integer.parseInt(strTime.substring(0, 2));
        int minutes = Integer.parseInt(strTime.substring(3, 5));
        return hours + minutes / 60.0;
    }
    public static double averageTime(ArrayList<Double> timeValues) {
        // I used a unit circle to represent all the time including the scenario between 11:59PM and midnight
        // since averaging the actual values of 23.9 (11:59PM) and 0.0 (12:00AM) would be 12PM instead of 23:59:30.
        double xSum = 0.0;
        double ySum = 0.0;
        for (double time : timeValues) {
            double angle = (time / 24.0) * 2 * Math.PI; //Converts to angles by turning the time of the day into a
            //decimal (by dividing 24) and then multiplying it by 2pi, which gets the angle in radians on the unit circle
            xSum += Math.cos(angle); //Horizontal length of the unit circle
            ySum += Math.sin(angle); //Vertical length of the unit circle
        }
        double avgX = xSum / timeValues.size(); //Takes the average of all lengths on the horizontal component
        double avgY = ySum / timeValues.size(); //Same for the vertical component
        double avgRadians = Math.atan2(avgY, avgX); //uses Tangent function to determine its average angle where
        //it averages the x and y length, which corresponds to a certain time in hh:mm
        double averageTime = (avgRadians / (2 * Math.PI)) * 24; //Converts back to the 0-24hr format by
        // reversing the original radians conversion formula, now it returns a double
        // value, again, representing the time but average time instead!
        if (averageTime < 0) { // Ensures a positive value regardless
            averageTime += 24; //If negative, add 1 day (24 hours)
        }
        return averageTime; //That's it!
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
