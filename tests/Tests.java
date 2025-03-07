import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @Test
    public void testRecordArrayWithMockedUserInput() {
    }

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
        double[] actual = Main.calculateAverages(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void avgTwo() {
        ArrayList<double[]> input = new ArrayList<>();
        input.add(new double[]{-1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3});
        input.add(new double[]{-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
        double[] expected = {-1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        double[] actual = Main.calculateAverages(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void avgFour_and_a_half_with_great_variation_in_each_data_point() {
        ArrayList<double[]> input = new ArrayList<>();
        input.add(new double[]{-1, 7, 5, 2, 4, 8, 9, 3, 3, 6, 4.5});
        input.add(new double[]{-1, 2, 4, 7, 5, 1, 0, 6, 6, 3, 4.5});
        double[] expected = {-1, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5, 4.5};
        double[] actual = Main.calculateAverages(input);
        assertArrayEquals(expected, actual);
    }


    @Test
    public void testCalculateAveragesMultipleEntries() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
        userData.add(new double[]{-1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 11});
        double[] result = Main.calculateAverages(userData);
        assertArrayEquals(new double[]{-1, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5, 10.5, 11}, result);
    }

    @Test
    public void testSleepQualityFactorsImproving() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 8, 18, 8, 60, 20, 3, 12, 2000, 22, 6});
        Main.sleepQualityFactors(userData);
    }

    @Test
    public void testSleepQualityFactorsReducing() {
        ArrayList<double[]> userData = new ArrayList<>();
        userData.add(new double[]{-1, 3, 20, 5, 30, 60, 5, 24, 1000, 23, 7});
        Main.sleepQualityFactors(userData);
        
    }
}