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

            int choice = validateInput(1, 3);

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
                    System.out.println("Enter your screen time today in hours (Valid input range is 0-24):");
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
                    System.out.println("Enter the time you went to bed in 24-hour format. (e.g. 02:00, 23:00, 23:59)");
                    res[9] = validateTime();
                    System.out.println("Enter the time you woke up. (e.g. 06:00, 08:38, 11:00)");
                    res[10] = validateTime();
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
        System.out.println("2) Show factors affecting sleep quality.");
        System.out.println("3) Show factors affecting sleep duration.");
        System.out.println("4) Show averages of recorded data.");
        System.out.println("5) Show relative consistency of sleep/wake times.");
        System.out.println("6) Exit.");
        System.out.println("---------------------–-----------------------------");

        int choice = validateInput(1, 6);

        switch (choice) {
            case 1:
                displayData(userData);
                break;
            case 2:
                sleepQualityFactors(userData);
                break;
            case 3:
                sleepDurationFactors(userData);
                break;
            case 4:
                ArrayList<double[]> avg = new ArrayList<>();
                avg.add(calculateAverages(userData));
                displayData(avg);
                break;
            case 5:
                sleepWakeConsistency(userData);
                break;
            case 6:
                break;
        }
    }

    public static void displayData(ArrayList<double[]> userData) {
        System.out.println("---------------------–-----------------------------");
        displayRow("Sleep quality (1-10):\t", userData, 1);
        displayRow("Room temperature (°C):\t", userData, 2);
        displayRow("Sleep duration (hrs):\t", userData, 3);
        displayRow("Cardio duration (mins):\t", userData, 4);
        displayRow("Napping duration (mins):", userData, 5);
        displayRow("Screen time (hrs):\t\t", userData, 6);
        displayRow("Mattress age (months):\t", userData, 7);
        displayRow("Water intake (mL):\t\t", userData, 8);
        displayRow("Wake time (24hr):\t\t", userData, 9);
        displayRow("Sleep time (24hr):\t\t", userData, 10);
        System.out.println("---------------------–-----------------------------");
    }

    public static void sleepQualityFactors(ArrayList<double[]> userData) {
        int cardioUpCount = 0, cardioDownCount = 0;
        int tempUpCount = 0, tempDownCount = 0;
        int screenTimeUpCount = 0, screenTimeDownCount = 0;
        int waterUpCount = 0, waterDownCount = 0;
        int napUpCount = 0, napDownCount = 0;
        int sleep_durationUpCount = 0, sleep_durationDownCount = 0;
        //Up and Down in the naming represents the number of times a certain variable causes
        //an increase or decrease in sleep quality
        for (double[] log : userData) { //Sleep quality always starts at index 1
            double sleepQuality = log[1];
            double cardio = log[4];
            double temperature = log[2];
            double screenTime = log[6];
            double water = log[8];
            double nap_duration = log[5];
            double sleep_duration = log[3];
            if (cardio >= 60) {
                if (sleepQuality >= 7.5) {
                    cardioUpCount++; //7.5 out of 10 sleep quality indicates a solid rest
                } else if (sleepQuality <= 3.5) {
                    cardioDownCount++; //3.5 out of 10 sleep quality indicates a restless night
                }
            } else if (cardio < 60) {
                if (sleepQuality >= 7.5) {
                    cardioUpCount++;
                } else if (sleepQuality <= 3.5) {
                    cardioDownCount++; //Ends here for cardio
                }
            }
            if (temperature <= 18.5) {
                if (sleepQuality >= 7.5) {
                    tempUpCount++;
                } else if (sleepQuality <= 3.5) {
                    tempDownCount++;
                }
            } else if (temperature > 18.5) {
                if (sleepQuality >= 7.5) {
                    tempUpCount++;
                } else if (sleepQuality <= 3.5) {
                    tempDownCount++;
                }
            }
            if (screenTime <= 4) {
                if (sleepQuality >= 7.5) {
                    screenTimeUpCount++;
                } else if (sleepQuality <= 3.5) {
                    screenTimeDownCount++;
                }
            } else if (screenTime > 4) {
                if (sleepQuality >= 7.5) {
                    screenTimeUpCount++;
                } else if (sleepQuality <= 3.5) {
                    screenTimeDownCount++;
                }
            }
            if (water >= 2000) {
                if (sleepQuality >= 7.5) {
                    waterUpCount++;
                } else if (sleepQuality <= 3.5) {
                    waterDownCount++;
                }
            } else if (water < 2000) {
                if (sleepQuality >= 7.5) {
                    waterUpCount++;
                } else if (sleepQuality <= 3.5) {
                    waterDownCount++;
                }
            }
            if (nap_duration <= 30) {
                if (sleepQuality >= 7.5) {
                    napUpCount++;
                } else if (sleepQuality <= 3.5) {
                    napDownCount++;
                }
            } else if (nap_duration > 30) {
                if (sleepQuality >= 7.5) {
                    napUpCount++;
                } else if (sleepQuality <= 3.5) {
                    napDownCount++;
                }
            }
            if (sleep_duration >= 8) {
                if (sleepQuality >= 7.5) {
                    sleep_durationUpCount++;
                } else if (sleepQuality <= 3.5) {
                    sleep_durationDownCount++;
                }
            } else if (sleep_duration < 8) {
                if (sleepQuality >= 7.5) {
                    sleep_durationUpCount++;
                } else if (sleepQuality <= 3.5) {
                    sleep_durationDownCount++;
                }
            }
        }
        System.out.println("Factors that improved sleep quality:");
        System.out.println(" - More than 60 min cardio: " + cardioUpCount);
        System.out.println(" - Room temp below 18.5°C: " + tempUpCount);
        System.out.println(" - Screen time less than 4 hours: " + screenTimeUpCount);
        System.out.println(" - Drinking 2L+ water: " + waterUpCount);
        System.out.println(" - Short naps (<30 min): " + napUpCount);
        System.out.println(" - Enough sleep (>8 hours): " + sleep_durationUpCount);

        System.out.println("Factors that reduced sleep quality:");
        System.out.println(" - Less than 60 min cardio: " + cardioDownCount);
        System.out.println(" - Room temp above 18.5°C: " + tempDownCount);
        System.out.println(" - Screen time 4+ hours: " + screenTimeDownCount);
        System.out.println(" - Low water intake: " + waterDownCount);
        System.out.println(" - Long naps (>30 min): " + napDownCount);
        System.out.println(" - Insufficient sleep (<8 hours): " + sleep_durationDownCount);

    }

    private static void sleepDurationFactors(ArrayList<double[]> userData) {
        {

            int increaseCardio = 0, decreaseCardio = 0;
            int increaseTemp = 0, decreaseTemp = 0;
            int increaseScreenTime = 0, decreaseScreenTime = 0;
            int increaseWater = 0, decreaseWater = 0;
            int increaseNap = 0, decreaseNap = 0;

            for (double[] log : userData) {
                double sleepHours = log[3];
                double cardioMinutes = log[4];
                double roomTemp = log[2];
                double screenHours = log[6];
                double waterIntake = log[8];
                double napMinutes = log[5];

                if (cardioMinutes >= 60) {
                    if (sleepHours >= 8) {
                        increaseCardio++;
                    } else if (sleepHours <= 5) {
                        decreaseCardio++;
                    }
                } else if (cardioMinutes < 60) {
                    if (sleepHours >= 8) {
                        increaseCardio++;
                    } else if (sleepHours <= 5) {
                        decreaseCardio++;
                    }
                }
                if (roomTemp <= 18.5) {
                    if (sleepHours >= 8) {
                        increaseTemp++;
                    } else if (sleepHours <= 5) {
                        decreaseTemp++;
                    }
                } else if (roomTemp > 18.5) {
                    if (sleepHours >= 8) {
                        increaseTemp++;
                    } else if (sleepHours <= 5) {
                        decreaseTemp++;
                    }
                }
                if (screenHours <= 4) {
                    if (sleepHours >= 8) {
                        increaseScreenTime++;
                    } else if (sleepHours <= 5) {
                        decreaseScreenTime++;
                    }
                } else if (screenHours > 4) {
                    if (sleepHours >= 8) {
                        increaseScreenTime++;
                    } else if (sleepHours <= 5) {
                        decreaseScreenTime++;
                    }
                }
                if (waterIntake >= 2000) {
                    if (sleepHours >= 8) {
                        increaseWater++;
                    } else if (sleepHours <= 5) {
                        decreaseWater++;
                    }
                } else if (waterIntake < 2000) {
                    if (sleepHours >= 8) {
                        increaseWater++;
                    } else if (sleepHours <= 5) {
                        decreaseWater++;
                    }
                }
                if (napMinutes <= 30) {
                    if (sleepHours >= 8) {
                        increaseNap++;
                    } else if (sleepHours <= 5) {
                        decreaseNap++;
                    }
                } else if (napMinutes > 30) {
                    if (sleepHours >= 8) {
                        increaseNap++;
                    } else if (sleepHours <= 5) {
                        decreaseNap++;
                    }
                }
            }

            System.out.println("Factors that improved sleep duration:");
            System.out.println(" - More than 60 min cardio: " + increaseCardio);
            System.out.println(" - Room temp below 18.5°C: " + increaseTemp);
            System.out.println(" - Screen time less than 4 hours: " + increaseScreenTime);
            System.out.println(" - Drinking 2L+ water: " + increaseWater);
            System.out.println(" - Short naps (<30 min): " + increaseNap);

            System.out.println("Factors that reduced sleep duration:");
            System.out.println(" - Less than 60 min cardio: " + decreaseCardio);
            System.out.println(" - Room temp above 18.5°C: " + decreaseTemp);
            System.out.println(" - Screen time 4+ hours: " + decreaseScreenTime);
            System.out.println(" - Low water intake: " + decreaseWater);
            System.out.println(" - Long naps (>60 min): " + decreaseNap);
        }
    }

    private static double[] calculateAverages(ArrayList<double[]> userData) {
        double[] avg = new double[userData.getFirst().length];
        for (double[] log : userData)
            for (int i = 0; i < log.length; i++)
                avg[i] += log[i];
        int numDays = userData.size();
        for (int i = 0; i < avg.length; i++)
            avg[i] /= numDays;
        return avg;
    }

    private static void sleepWakeConsistency(ArrayList<double[]> userData) {
        if (userData.isEmpty()) {
            System.out.println("No logs recorded so far! Please record at least one night's worth of data.");
            return;
        }
        double totalBedtimeDifference = 0;
        double totalWakeTimeDifference = 0;
        int numberOfNights = userData.size();

        for (int i = 1; i < numberOfNights; i++) {
            double[] previousNight = userData.get(i - 1);
            double[] currentNight = userData.get(i);

            double previousSleepTime = previousNight[9];
            double currentSleepTime = currentNight[9];
            double previousWakeTime = previousNight[10];
            double currentWakeTime = currentNight[10];

            totalBedtimeDifference += Math.abs(currentSleepTime - previousSleepTime);
            totalWakeTimeDifference += Math.abs(currentWakeTime - previousWakeTime);
        }

        double averageBedtimeDifference = totalBedtimeDifference / (numberOfNights - 1);
        double averageWakeTimeDifference = totalWakeTimeDifference / (numberOfNights - 1);

        System.out.println("---------------------–-----------------------------");
        System.out.println("Relative consistency of sleep/wake times:");
        System.out.println("Average difference in sleep times: " + showTime(averageBedtimeDifference));
        System.out.println("Average difference in wake times: " + showTime(averageWakeTimeDifference));
        System.out.println("---------------------–-----------------------------");
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

    public static double validateTime() {
        Scanner input = new Scanner(System.in);
        double res = 0;

        while (true) {
            try {
                String strTime = input.next();
                res += Integer.parseInt(strTime.substring(0, 2));
                res += Double.parseDouble(strTime.substring(3, 5)) / 60;
                return res;
            } catch (InputMismatchException | NumberFormatException | StringIndexOutOfBoundsException e) {
                System.out.println("Please enter a valid time in 24 hour format. (e.g. 02:00, 14:00, 22:59)");
                res = 0;
            }
        }
    }

    public static void displayRow(String message, ArrayList<double[]> userData, int row) {
        StringBuilder res = new StringBuilder();
        res.append(message);
        res.append("\t");
        if (9 <= row && row <= 10) {
            for(double[] night : userData)
                res.append(showTime(night[row])).append("\t");
        } else {
            for(double[] night : userData)
                res.append(round(night[row])).append("\t");
        }
        System.out.println(res);
    }

    public static String round(double num) { // returns number normalized to 5 characters wide.
        if (num >= 10000) {
            return String.valueOf(Math.round(num));
        } else if (num >= 1000) {
            return " " + Math.round(num);
        } else if (num >= 100) {
            return String.format("%.1f", num);
        } else if (num >= 10) {
            return String.format("%.2f", num);
        } else {
            return String.format("%.3f", num);
        }
    }

    public static String showTime(double timeNum) {
        String res = "";
        res += String.format("%02d", (int) timeNum);
        res += ":";
        int mins = (int) ((timeNum - ((int) timeNum)) * 60);
        res += String.format("%02d", mins);
        return res;
    }
}
