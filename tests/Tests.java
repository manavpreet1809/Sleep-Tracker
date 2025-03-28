import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @Test
    public void oneDigitRound() {
        String expected = "2.000";
        String actual = Main.round(2);
        assertEquals(expected, actual);
    }

    @Test
    public void twoDigitRound() {
        String expected = "37.89";
        String actual = Main.round(37.89);
        assertEquals(expected, actual);
    }

    @Test
    public void threeDigitRound() {
        String expected = "101.0";
        String actual = Main.round(101);
        assertEquals(expected, actual);
    }

    @Test
    public void fourDigitRound() {
        String expected = " 2379";
        String actual = Main.round(2379.31412);
        assertEquals(expected, actual);
    }

    @Test
    public void fiveDigitRound() {
        String expected = "10000";
        String actual = Main.round(10000.00);
        assertEquals(expected, actual);
    }

    @Test
    public void showTimeValid() {
        String expected = "22:30";
        String actual = Main.showTime(22.5);
        assertEquals(expected, actual);
    }

    @Test
    public void showTimeMidnight() {
        String expected = "00:00";
        String actual = Main.showTime(0.00);
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

    // TODO: sampleVariance() tests
    // TODO: read() tests
    // TODO: load() tests
    @Test
    public void sampleVariance() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{1});
        userData.add(new double[]{9});
        userData.add(new double[]{3});
        double expected = 17.0;
        double actual = Math.round(User.sampleVariance(userData, 0));
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
        double actual = new TimeValidator().validate(0, 24);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void validateStandardTime_Midnight() {
        System.setIn(new ByteArrayInputStream("00:00\n".getBytes()));
        double expected = 0.0;
        double actual = new TimeValidator().validate(0, 24);
        assertEquals(expected, actual, 0.001);
    }
}