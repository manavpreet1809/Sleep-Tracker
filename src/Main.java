import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static String PATH = "save/data.csv";

    public static void main(String[] args) {
        User user = new User(new ArrayList<>());
        Menu.displayMenu(user);
    }

    /**
     * Loads user data from a .csv file.
     * Watch out! This method may throw FileNotFound exception!
     * @return Loaded sleep logs.
     */
    public static ArrayList<double[]> load() throws FileNotFoundException {
        File f = new File(PATH);
        ArrayList<double[]> res = new ArrayList<>();
        Scanner input = new Scanner(f);
        input.nextLine(); // skip header.
        while (input.hasNextLine()) {
            String line = input.nextLine();
            String[] splitLine = line.split(",");
            double[] numLine = new double[splitLine.length];
            for (int i = 0; i < splitLine.length; i++) {
                numLine[i] = Double.parseDouble(splitLine[i]);
            }
            res.add(numLine);
        }
        return res;
    }

    /**
     * Saves user data to a .csv file.
     * @param userData User's sleep logs.
     */
    public static void save(ArrayList<double[]> userData) {
        File f = new File(PATH);
        try {
            FileWriter fw = new FileWriter(f);
            // header
            fw.append("(empty),Sleep quality,Room temperature,Sleep duration,Time cardio,Time napping,Time screen,Mattress age,Water intake,Sleep time,Wake time\n");
            for (double[] log : userData) {
                for (double i : log) {
                    for (char c : String.valueOf(i).toCharArray()) {
                        fw.append(c);
                    }
                    fw.append(",");
                }
                fw.append("\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Failed to write to the file.");
        }
    }

    /**
     * Takes a number representing time in "decimal format" and returns a string with the time in
     * classic 24 hr format.
     * @param timeNum decimal number representing a time (used internally)
     * @return time in 24 hr format
     */
    public static String showTime(double timeNum) {
        String res = "";
        res += String.format("%02d", (int) timeNum);
        res += ":";
        int minutes = (int) ((timeNum - ((int) timeNum)) * 60);
        res += String.format("%02d", minutes);
        return res;
    }

    /**
     * A bespoke function which takes a double and returns a String representation that is
     * exactly 5 characters wide.
     * @param num Input number, must be >100000.
     * @return String representation.
     */
    public static String round(double num) {
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




}
