import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean run = true;
        while (run) {
            System.out.println("Welcome to our sleep tracker application!");
            System.out.println("Please choose if you would like to:");
            System.out.println("1) Record a new log");
            System.out.println("2) Analyze trends");
            System.out.println("3) Exit program");

            Scanner input = new Scanner(System.in);
            int choice = input.nextInt();
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
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }

        }
    }

    public static void record() {
        System.out.println("Does nothing for now :P");
    }

    public static void analyze() {
        System.out.println("Does nothing for now :P");
    }
}
