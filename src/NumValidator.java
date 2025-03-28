import java.util.InputMismatchException;
import java.util.Scanner;

public class NumValidator implements Validator {
    /**
     * @param min Minimum allowed value.
     * @param max Maximum allowed value.
     * @return Validated user input.
     */
    @Override
    public double validate(double min, double max) {
        Scanner input = new Scanner(System.in);
        boolean valid = false;
        int res = 0;

        if (min > max) {
            double temp = min;
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
