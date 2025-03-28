import java.util.InputMismatchException;
import java.util.Scanner;

public class TimeValidator implements Validator {

    /**
     * Error trapping function which repeatedly prompts user until they enter a valid time.
     * The time must be entered in 24-hour format (e.g. 02:00, 14:56).
     * @return A decimal value which maps to a certain time. e.g. 10:30 -> 10.5
     */
    @Override
    public double validateStream(double min, double max) {
        Scanner input = new Scanner(System.in);
        double res = 0;

        if (min > max) {
            double temp = min;
            min = max;
            max = temp;
        }

        while (true) {
            try {
                String strTime = input.next();
                res += Integer.parseInt(strTime.substring(0, 2));
                res += Double.parseDouble(strTime.substring(3, 5)) / 60;
                if (res < min || res > max)
                    throw new NumberFormatException();
                return res;
            } catch (InputMismatchException | NumberFormatException | StringIndexOutOfBoundsException e) {
                System.out.println("Please enter a valid time in 24 hour format. (e.g. 02:00, 14:00, 22:59)");
                res = 0;
            }
        }
    }

    @Override
    public boolean validateVariable(Object o, double min, double max) {
        if (!(o instanceof String)) {
            return false;
        }
        double timeNum = 0;
        timeNum += Integer.parseInt(((String) o).substring(0, 2));
        timeNum += Double.parseDouble(((String) o).substring(3, 5)) / 60;
        return timeNum >= min && timeNum <= max;
    }
}
