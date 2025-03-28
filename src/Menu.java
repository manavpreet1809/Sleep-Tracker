import java.util.ArrayList;

public class Menu {
    public static void displayMenu(User user) {
        boolean run = true;
        while (run) {
            System.out.println("---------------------–-----------------------------");
            System.out.println("\tWelcome to our sleep tracker application!");
            System.out.println("\tPlease choose whether you would like to:");
            System.out.println("\t1) Record a new log");
            System.out.println("\t2) Analyze trends");
            System.out.println("\t3) Save logs so far to a file.");
            System.out.println("\t4) Load saved logs.");
            System.out.println("\t5) Exit the program.");
            System.out.println("---------------------–-----------------------------");

            NumValidator nv = new NumValidator();
            int choice = (int) nv.validateStream(1, 5);

            switch (choice) {
                case 1:
                    user.addRow(record());
                    break;
                case 2:
                    analyze(user);
                    break;
                case 3:
                    Main.save(user.getUserData());
                    break;
                case 4:
                    try {
                        User loaded = Main.load();
                        if (loaded.equals(user)) {
                            System.out.println("File already loaded!"); // using equals here
                        } else {
                            user = loaded;
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to load sleep data! Make sure you have saved at least once.");
                    }
                    break;
                default:
                    run = false;
                    break;
            }
        }
    }


    /**
     * Records a new sleep log based on user input of the sleep-related variables.
     *
     * @return A double array which has the user's recorded sleep data.
     */
    public static double[] record() {
        double[] res = new double[11]; // 0th index is kept blank for readability.
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

            NumValidator nv = new NumValidator();
            TimeValidator tv = new TimeValidator();

            int variable_chosen = (int) nv.validateStream(1, 10);
            switch (variable_chosen) {
                case 1: //Each variable is stored into res[] (indexes 1-10), to 2 decimal places
                    System.out.println("Enter your sleep quality from a scale of 1-10 (1=Restless, 10=Highly restorative):");
                    res[Factor.SLEEP_QUALITY.getArrayIndex()] = (int) nv.validateStream(1, 10);
                    break;
                case 2:
                    System.out.println("Enter your room temperature in °C (Valid input range is 10.0-30.0):");
                    res[Factor.ROOM_TEMPERATURE.getArrayIndex()] = nv.validateStream(10.0, 30.0);
                    break;
                case 3:
                    System.out.println("Enter your sleep duration in hours (Valid input range is 0-24):");
                    res[Factor.SLEEP_DURATION.getArrayIndex()] = nv.validateStream(0, 24); //User cannot sleep more than 24 hours per day
                    break;
                case 4:
                    System.out.println("Enter the amount of time you spent on doing cardio today in minutes (Valid input range is 0-1380):");
                    res[Factor.CARDIO_DURATION.getArrayIndex()] = nv.validateStream(0, 1380);
                    break;
                case 5:
                    System.out.println("Enter the amount of time you spent napping today in minutes (Valid input range is 0-180):");
                    res[Factor.NAPPING_DURATION.getArrayIndex()] = nv.validateStream(0, 180); //3 hrs is the longest to be considered a nap
                    break;
                case 6:
                    System.out.println("Enter your screen time today in hours (Valid input range is 0-24):");
                    res[Factor.SCREEN_TIME.getArrayIndex()] = nv.validateStream(0, 24);
                    break;
                case 7:
                    System.out.println("Enter the age of your mattress in months just ONCE (Valid input range is 0-1560):");
                    res[Factor.MATTRESS_AGE.getArrayIndex()] = nv.validateStream(0, 1560);
                    break;
                case 8:
                    System.out.println("What is your water intake? Enter in mL (Valid input range is 0-12000):");
                    res[Factor.WATER_INTAKE.getArrayIndex()] = nv.validateStream(0, 12000);
                    break;
                case 9:
                    System.out.println("Enter the time you went to bed in 24-hour format. (e.g. 02:00, 23:00, 23:59)");
                    res[Factor.SLEEP_TIME.getArrayIndex()] = tv.validateStream(0,24);
                    System.out.println("Enter the time you woke up. (e.g. 06:00, 08:38, 11:00)");
                    res[Factor.WAKE_TIME.getArrayIndex()] = tv.validateStream(0,24);
                    break;
                case 10:
                    System.out.println("Congratulations, you completed your log for today!");
                    run = false;
                    break;  //No need for a data entry here
            }
        }
        return res;
    }

    /**
     * Analyze the recorded sleep data based on user's desired preferences.
     * @param user User object to analyze.
     */
    public static void analyze(User user) {
        if (user.getUserData().isEmpty()) {
            System.out.println("No logs recorded so far! Please record at least one night's worth of data.");
            return;
        }

        System.out.println("---------------------–-----------------------------");
        System.out.println("What data would you like to see/analyse?");
        System.out.println("1) Show all data recorded so far.");
        System.out.println("2) Show factors affecting sleep quality.");
        System.out.println("3) Show factors affecting sleep duration.");
        System.out.println("4) Show averages of recorded data.");
        System.out.println("5) Show relative consistency of sleep/wake times.");
        System.out.println("6) Show last night's sleep duration in sleep cycles.");
        System.out.println("7) Show last night's sleep efficiency.");
        System.out.println("8) Exit.");
        System.out.println("---------------------–-----------------------------");

        NumValidator nv = new NumValidator();
        int choice = (int) nv.validateStream(1, 8);

        switch (choice) {
            case 1:
                displayData(user.getUserData());
                break;
            case 2:
                User.sleepQualityFactors(user.getUserData());
                break;
            case 3:
                User.sleepDurationFactors(user.getUserData());
                break;
            case 4:
                ArrayList<double[]> avg = new ArrayList<>();
                avg.add(User.calculateAverages(user.getUserData()));
                displayData(avg);
                break;
            case 5:
                printSleepWakeConsistency(user.getUserData());
                break;
            case 6:
                int sleepCycles = User.sleepCycles(user.getUserData());
                System.out.printf("Last night, you slept for %d sleep cycles.%n", sleepCycles);
                break;
            case 7:
                int efficient = User.REM_Duration(user.getUserData());
                System.out.println("Last night, your Rapid Eye Movement duration in minutes was.%n:" + efficient);
            default:
                break;
        }
    }


    /**
     * Analyze the recorded sleep data based on user's desired preferences.
     *
     * @param userData The list of user's recorded sleep logs to be analyzed.
     */
    public static void displayData(ArrayList<double[]> userData) {
        System.out.println("---------------------–-----------------------------");
        displayRow("Sleep quality (1-10):\t", userData, 1);
        displayRow("Room temperature (°C):\t", userData, 2);
        displayRow("Sleep duration (hrs):\t", userData, 3);
        displayRow("Cardio duration (minutes):", userData, 4);
        displayRow("Napping duration (minutes):", userData, 5);
        displayRow("Screen time (hrs):\t\t", userData, 6);
        displayRow("Mattress age (months):\t", userData, 7);
        displayRow("Water intake (mL):\t\t", userData, 8);
        displayRow("Wake time (24hr):\t\t", userData, 9);
        displayRow("Sleep time (24hr):\t\t", userData, 10);
        System.out.println("---------------------–-----------------------------");
    }

    /**
     * Displays one row of data. The row can contain either numbers or times.
     * @param message A description of what the data is (justified).
     * @param userData Dynamic array of data.
     * @param row Row index to display.
     */
    public static void displayRow(String message, ArrayList<double[]> userData, int row) {
        StringBuilder res = new StringBuilder();
        res.append(message);
        res.append("\t");
        if (9 <= row && row <= 10) {
            for(double[] night : userData) {
                Time t = new Time(night[row]);
                res.append(t).append("\t");
            }
        } else {
            for(double[] night : userData)
                res.append(Main.round(night[row])).append("\t");
        }
        System.out.println(res);
    }

    /**
     * Analyzes and displays the consistency of sleep and wake times based on recorded sleep data.
     * Uses sample variance calculation.
     *
     * @param userData The list of recorded sleep logs.
     */
    private static void printSleepWakeConsistency(ArrayList<double[]> userData) {
        ArrayList<double[]> copy = new ArrayList<>();
        for (double[] log : userData) {
            copy.add(log.clone());
            if (copy.getLast()[9] < 12) {
                copy.getLast()[9] += 24;
            }
        }

        double sleepVariance = User.sampleVariance(copy, 9);
        double wakeVariance = User.sampleVariance(copy, 10);
        if (sleepVariance > 10) {
            System.out.println("Your sleep times are highly variable.");
        } else {
            System.out.println("Your sleep times are consistent!");
        } if (wakeVariance > 10) {
            System.out.println("Your wake times are highly variable.");
        } else {
            System.out.println("Your wake times are consistent!");
        }
    }
}
