import java.util.ArrayList;

public class User {
    private ArrayList<double[]> userData;

    public User(ArrayList<double[]> userData) {
        this.userData = userData;
    }

    public ArrayList<double[]> getUserData() {
        return this.userData;
    }

    public void setUserData(ArrayList<double[]> userData) {
        this.userData = userData;
    }

    /**
     * Multiplies last sleep duration by 2/3 and truncates to get whole number of sleep cycles.
     * @return Number of sleep cycles.
     */
    public static int sleepCycles(ArrayList<double[]> userData) {
        double[] lastEntry = userData.getLast();
        return (int) (lastEntry[3] * 2 / 3);
    }

    /**
     * Analyzes and displays the factors that affect sleep duration based on the recorded sleep data.
     *
     * @param userData The list of user's recorded sleep logs, where each entry is a double array containing sleep related variables.
     */
    public static double[] calculateAverages(ArrayList<double[]> userData) {
        double[] avg = new double[userData.getFirst().length];
        for (double[] log : userData)
            for (int i = 0; i < log.length; i++)
                avg[i] += log[i];
        int numDays = userData.size();
        for (int i = 0; i < avg.length; i++)
            avg[i] /= numDays;
        return avg;
    }

    /**
     * Allows user of the class to add a row to userData.
     * @param row Row to add.
     */
    public void addRow(double[] row) {
        this.userData.add(row);
    }

    /**
     * Calculates expected value of squared deviation from the mean using a known mathematical formula.
     * @param dataset User data.
     * @param row Row to calculate variance on.
     * @return Squared deviation.
     */
    public static double sampleVariance(ArrayList<double[]> dataset, int row) {
        double avg = calculateAverages(dataset)[row];
        double sum = 0;
        for (double[] log : dataset)
            sum += Math.pow(avg - log[row], 2);
        return sum / (dataset.size() - 1);
    }
}
