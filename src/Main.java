import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean run = true;
        ArrayList<double[]> userData = new ArrayList<>();

        while (run) {
            System.out.println("---------------------–-----------------------------");
            System.out.println("\tWelcome to our sleep tracker application!");
            System.out.println("\tPlease choose whether you would like to:");
            System.out.println("\t1) Record a new log");
            System.out.println("\t2) Analyze trends");
            System.out.println("\t3) Exit program");
            System.out.println("---------------------–-----------------------------");

            int choice = validateInput(1,3);

            switch (choice) {
                case 1:
                    userData.add(record());
                    break;
                case 2:
                    analyze(userData);
                    break;
                case 3:
                    run = false;
                    break;
            }
        }
    }

    public static double[] record() {
        double[] res = new double[20]; // 0th index is kept blank for readability.
        boolean run = true;

        while (run) {
            System.out.println("---------------------–-----------------------------");
            System.out.println("\tYou may enter any number of variables that you would like to log for this night.");
            System.out.println("\t1) Sleep quality (Scale of 1-10)");
            System.out.println("\t2) Room temperature (in °C)");
            System.out.println("\t3) Sleep duration (in hrs)");
            System.out.println("\t4) Time spent doing cardio over the day (in minutes)");
            System.out.println("\t5) Time spent napping during the day (in minutes)");
            System.out.println("\t6) Screen time (in hrs)");
            System.out.println("\t7) Age of mattress in months (only enter once)");
            System.out.println("\t8) Water intake (in mL)");
            System.out.println("\t9) Sleep/wake times (AM/PM)");
            System.out.println("\t10) Finish logging");
            System.out.println("---------------------–-----------------------------");

            int variable_chosen = validateInput(1, 10);
            switch (variable_chosen) {
                case 1: //Each variable is stored into res[] (indexes 1-10), to 2 decimal places
                    System.out.println("Enter your sleep quality from a scale of 1-10 (1=Restless, 10=Highly restorative):");
                    res[1] = validateInput(1, 10);
                    break;
                case 2:
                    System.out.println("Enter your room temperature in °C (Valid input range is 10.0-30.0):");
                    res[2] = validateInput(10.0, 30.0);
                    break;
                case 3:
                    System.out.println("Enter your sleep duration in hours (Valid input range is 0-24):");
                    res[3] = validateInput(0, 24); //User cannot sleep more than 24 hours per day
                    break;
                case 4:
                    System.out.println("Enter the amount of time you spent on doing cardio today in minutes (Valid input range is 0-1380):");
                    res[4] = validateInput(0, 1380);
                    break;
                case 5:
                    System.out.println("Enter the amount of time you spent napping today in minutes (Valid input range is 0-180):");
                    res[5] = validateInput(0, 180); //3 hrs is the longest to be considered a nap
                    break;
                case 6:
                    System.out.println("Enter your Screen time today in hours (Valid input range is 0-24):");
                    res[6] = validateInput(0, 24);
                    break;
                case 7:
                    System.out.println("Enter the age of your mattress in months just ONCE (Valid input range is 0-1560):");
                    res[7] = validateInput(0, 1560);
                    break;
                case 8:
                    System.out.println("What is your water intake? Enter in mL (Valid input range is 0-12000):");
                    res[8] = validateInput(0, 12000);
                    break;
                case 9:
                    System.out.println("Enter the time you went to bed (Next you will enter the time you woke up, enter both values in 24-hour-format, enter 22.50 (if 10:30PM) or 6.25 (if 6:15AM)");
                    res[9] = validateInput(0, 24);
                    System.out.println("Enter the time you woke up (Enter in 24-hour-format like last time, enter 22.50 (if 10:30PM) or 11.6 (if 11:36PM)");
                    res[10] = validateInput(0, 24);
                    break;
                case 10:
                    System.out.println("Congratulations, you completed your log for today!");
                    run = false;
                    break;  //No need for a data entry here
            }
        }
        return res;
    }

    public static void analyze(ArrayList<double[]> userData) {
        if (userData.isEmpty()) {
            System.out.println("No logs recorded so far! Please record at least one night's worth of data.");
            return;
        }

        System.out.println("---------------------–-----------------------------");
        System.out.println("What data would you like to see/analyse?");
        System.out.println("1) Show all data recorded so far.");
        System.out.println("---------------------–-----------------------------");

        int choice = validateInput(1,8);

        switch (choice) {
            case 1:
                System.out.println("---------------------–-----------------------------");
                displayRow("Sleep quality (1-10)", userData, 1);
                displayRow("Room temperature (°C)", userData, 2);
                System.out.println("---------------------–-----------------------------");

        }
    }

    public static int validateInput(int min, int max) {
        Scanner input = new Scanner(System.in);
        boolean valid = false;
        int res = 0;

        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }

        while (!valid) {
            try {
                res = input.nextInt();
                if (min <= res && res <= max)
                    valid = true;
                else
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number between " + min + " and " + max + ".");
                input.next();
            }
        }
        return res;
    }

    public static double validateInput(double min, double max) {
        Scanner input = new Scanner(System.in);
        boolean valid = false;
        double res = 0;

        if (min > max) {
            double temp = min;
            min = max;
            max = temp;
        }

        while (!valid) {
            try {
                res = input.nextDouble();
                if (min <= res && res <= max)
                    valid = true;
                else
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number between " + min + " and " + max + ".");
                input.next();
            }
        }
        return res;
    }

    public static void displayRow(String message, ArrayList<double[]> userData, int row) {
        StringBuilder res = new StringBuilder();
        res.append(message);
        res.append(":\t");
        for(double[] night : userData) {
            res.append(String.format("%.02f", night[row])).append("\t");
        }
        System.out.println(res);
    }
}
