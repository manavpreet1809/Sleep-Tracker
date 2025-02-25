import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean run = true;
        while (run) {
            System.out.println("************************************************");
            System.out.println("\tWelcome to our sleep tracker application!");
            System.out.println("\tPlease choose whether you would like to:");
            System.out.println("\t1) Record a new log");
            System.out.println("\t2) Analyze trends");
            System.out.println("\t3) Exit program");
            System.out.println("************************************************");

            int choice = validateInput(1,3);
            switch (choice) {
                case 1:
                    record();
                    break;
                case 2:
                    analyze();
                    break;
                case 3:
                    run = false;
                    break;
            }
        }
    }

    public static void record() {
        System.out.println("Does nothing for now :P");
        System.out.println("---------------------–-----------------------------");
        System.out.println("\tYou may enter any number of variables that you would like to log for this night.");
        System.out.println("\tChoose 1 of the following: ");
        System.out.println("\t1) Sleep quality (Scale of 1-10)");
        System.out.println("\t2) Room temperature (in °C)");
        System.out.println("\t3) Sleep duration (in hrs)");
        System.out.println("\t4) Time spent doing cardi over the day (in minutes)");
        System.out.println("\t5) Time spent napping during the day (in minutes)");
        System.out.println("\t6) Screen time (in hrs)");
        System.out.println("\t7) Age of mattress in months (only enter once)");
        System.out.println("\t8) Water intake (in mL)");
        System.out.println("\t9) Sleep/wake times (AM/PM)");
        System.out.println("\t10) Finish logging");

        int variable_chosen = validateInput(1,10);
        switch (variable_chosen) {
            case 1: 
            System.out.println("Enter your sleep quality from a scale of 1-10 (1=Restless, 10=Highly restorative):")
        }
    }

    public static void analyze() {
        System.out.println("Does nothing for now :P");
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
}
