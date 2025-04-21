package SleepTracker;

import SleepTracker.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MainShellTests {
    @Test
    public void oneDigitRound() {
        String expected = "2.000";
        String actual = MainShell.round(2);
        assertEquals(expected, actual);
    }

    @Test
    public void twoDigitRound() {
        String expected = "37.89";
        String actual = MainShell.round(37.89);
        assertEquals(expected, actual);
    }

    @Test
    public void threeDigitRound() {
        String expected = "101.0";
        String actual = MainShell.round(101);
        assertEquals(expected, actual);
    }

    @Test
    public void fourDigitRound() {
        String expected = " 2379";
        String actual = MainShell.round(2379.31412);
        assertEquals(expected, actual);
    }

    @Test
    public void fiveDigitRound() {
        String expected = "10000";
        String actual = MainShell.round(10000.00);
        assertEquals(expected, actual);
    }

    @Test
    public void showTimeValid() {
        String expected = "22:30";
        Time t = new Time(22.5);
        String actual = t.getTimeString();
        assertEquals(expected, actual);
    }

    @Test
    public void showTimeMidnight() {
        String expected = "00:00";
        Time t = new Time(0.00);
        String actual = t.getTimeString();
        assertEquals(expected, actual);
    }

    @Test
    public void avgZero() {
        ArrayList<double[]> input = new ArrayList<>();
        input.add(new double[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        double[] expected = {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] actual = User.calculateAverages(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void avgTwo() {
        ArrayList<double[]> input = new ArrayList<>();
        input.add(new double[]{-1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3});
        input.add(new double[]{-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        double[] expected = {-1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        double[] actual = User.calculateAverages(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void avgFourAndAHalfWithGreatVariationInEachDataPoint() {
        ArrayList<double[]> input = new ArrayList<>();
        input.add(new double[]{-1, 7, 5, 2, 4, 8, 9, 3, 3, 6, 4.5});
        input.add(new double[]{-1, 2, 4, 7, 5, 1, 0, 6, 6, 3, 4.5});
        double[] expected = {-1, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5};
        double[] actual = User.calculateAverages(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testSleepDurationFactorsIncreasing() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 9, 19, 8.5, 69, 0, 3, 216, 4750, 23.75, 7.51});
        userData.add(new double[]{-1, 1, 14, 0.5, 8, 0, 3, 216, 20, 7.5, 8.0});
        userData.add(new double[]{-1, 9, 19, 8.5, 69, 0, 3, 216, 4750, 23.75, 7.51});
        User.sleepDurationFactors(userData); //There should be 3 nights worth of data based on having
        //3 logs entered so far
        assertEquals(3, userData.size());
    }

    @Test
    public void testSleepDurationFactorsDecreasing() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 2.7, 23, 1, 0, 180, 12, 216, 10, 4.75, 7.45});
        User.sleepDurationFactors(userData);
    }


    @Test
    public void testCalculateAveragesMultipleEntries() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
        userData.add(new double[]{-1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 11});
        double[] result = User.calculateAverages(userData);
        assertArrayEquals(new double[]{-1, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5, 10.5, 11}, result);
    }

    @Test
    public void testSleepQualityFactorsImproving() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 8, 18, 8, 60, 20, 3, 12, 2000, 22, 6});
        User.sleepQualityFactors(userData);
    }

    @Test
    public void testSleepQualityFactorsReducing() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 3, 20, 5, 30, 60, 5, 24, 1000, 23, 7});
        User.sleepQualityFactors(userData);
    }

    @Test
    public void sixSleepCycles() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0});
        int expected = 6;
        int actual = User.sleepCycles(userData);
        assertEquals(expected, actual);
    }

    @Test
    public void lastNightSleepCycles() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0});
        int expected = 6;
        int actual = User.sleepCycles(userData);
        assertEquals(expected, actual);
    }

    @Test
    public void lastNightSleepCycles_for_7_and_a_half_hours_of_sleep() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 0, 7.5, 0, 0, 0, 0, 0, 0, 0});
        int expected = 5;
        int actual = User.sleepCycles(userData);
        assertEquals(expected, actual);
    }

    @Test
    public void sampleVarianceSingleEntry() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{0, 5.0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{0, 7.0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        double expected = 2.0;
        double actual = User.sampleVariance(userData, 1);
        assertEquals(expected, actual, 0.001);
    }


    @Test
    public void validateStandardTime() {
        System.setIn(new ByteArrayInputStream("14:30\n".getBytes()));
        double expected = 14.5;
        double actual = new TimeValidator().validateStream(0, 24);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void validateStandardTime_Midnight() {
        System.setIn(new ByteArrayInputStream("00:00\n".getBytes()));
        double expected = 0.0;
        double actual = new TimeValidator().validateStream(0, 24);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void Rapid_eye_movement() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, 0, 7.5, 0, 0, 0, 0, 0, 0});
        double expected = 150.0;
        double actual = User.REM_Duration(userData);
        assertEquals(expected, actual);
    }

    @Test
    public void Rapid_eye_movement_for_11_hours_of_sleep() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, 0, 11, 0, 0, 0, 0, 0, 0});
        double expected = 220.0;
        double actual = User.REM_Duration(userData);
        assertEquals(expected, actual);
    }

    @Test
    public void standard_deviation() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, 14, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 24, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 11, 7, 0, 0, 0, 0, 0, 0});
        double expected = 6.0;
        double actual = Math.round(User.standardDeviation(userData, 2));
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void standard_deviation_many_entries_to_verify_its_validity() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, 14, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 24, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 11, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 69, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 26, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 47, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 19, 7, 0, 0, 0, 0, 0, 0});
        double expected = 19.0;
        double actual = Math.round(User.standardDeviation(userData, 2));
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void standard_deviation_negative_numbers() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, -12, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, -2, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, -8, 7, 0, 0, 0, 0, 0, 0});
        double expected = 4.0;
        double actual = Math.round(User.standardDeviation(userData, 2));
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void standard_deviation_decimals() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 0, 25.56, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 24.24, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 17.65, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 107.11, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 75.25, 7, 0, 0, 0, 0, 0, 0});
        userData.add(new double[]{-1, 0, 69.71, 7, 0, 0, 0, 0, 0, 0});
        double expected = 33.0;
        double actual = Math.round((User.standardDeviation(userData, 2)));
        assertEquals(expected, actual, 0.001);
    }

    @Test
    void testEquals() {
        Time t1 = new Time(8.0);  // 08:00
        Time t2 = new Time("08:00");
        Time t3 = new Time(8.5);  // 08:30

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
    }

    @Test
    void testCompareTo() {
        Time t1 = new Time(7.0);  // 07:00
        Time t2 = new Time("07:30");
        Time t3 = new Time(8.0);  // 08:00

        assertTrue(t1.compareTo(t2) < 0);  // 07:00 < 07:30
        assertTrue(t2.compareTo(t3) < 0);  // 07:30 < 08:00
        assertEquals(0, t1.compareTo(new Time(7.0))); // Equal times
    }

    @Test
    void testNumValidator_ValidInput() {
        System.setIn(new ByteArrayInputStream("5\n".getBytes()));
        NumValidator validator = new NumValidator();
        assertEquals(5, validator.validateStream(1, 10));
    }


    @Test
    public void testDeepSleepMinutes() {
        ArrayList<double[]> logs = new ArrayList<>();
        logs.add(new double[]{-1, 9, 0, 8, 0, 0, 0, 0, 0, 0});
        User.SleepCycleInfo info = User.analyzeSleepCycles(logs);
        assertTrue(info.getDeepSleepMinutes() > 50);
    }

    @Test
    public void testDeepSleepMinutesLowQuality() {
        ArrayList<double[]> logs = new ArrayList<>();
        logs.add(new double[]{-1, 3, 0, 8, 0, 0, 0, 0, 0, 0});
        User.SleepCycleInfo info = User.analyzeSleepCycles(logs);
        assertTrue(info.getDeepSleepMinutes() > 40);
    }

    @Test
    public void averageTimeCrossingMidnight() {
        ArrayList<Double> times = new ArrayList<>();
        times.add(23.75);
        times.add(0.05);
        double expected = 23.90;
        double actual = Time.averageTime(times);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void averageTimeCrossingMidnight_Across_FiveNights() {
        ArrayList<Double> times = new ArrayList<>();
        times.add(23.75);
        times.add(0.05);
        times.add(3.05);
        times.add(1.99);
        times.add(22.01);
        double expected = 0.57;
        double actual = Time.averageTime(times);
        assertEquals(expected, actual, 0.01);
    }

    @Test
    public void averageTimeWithoutCrossingMidnight() {
        ArrayList<Double> times = new ArrayList<>();
        times.add(23.75);
        times.add(23.05);
        times.add(21.11);
        times.add(20.99);
        times.add(22.55);
        double expected = 22.29;
        double actual = Time.averageTime(times);
        assertEquals(expected, actual, 0.001);
    }
}
