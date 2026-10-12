package io.github.manavpreet1809.sleeptracker.analytics;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Predicts tonight's sleep quality from someone's own history with ridge regression: a linear
 * model on standardized habits whose penalty keeps coefficients small when there are few nights.
 */
public final class QualityModel {

    /** Habits the model may use. */
    public static final List<SleepFactor> CANDIDATES = List.of(
            SleepFactor.SLEEP_DURATION, SleepFactor.ROOM_TEMPERATURE, SleepFactor.CARDIO,
            SleepFactor.NAPPING, SleepFactor.SCREEN_TIME, SleepFactor.WATER_INTAKE);

    /** Fewer complete nights than this cannot support six coefficients. */
    public static final int MINIMUM_NIGHTS = 7;

    /** A habit is used only if recorded on at least this share of nights. */
    static final double MINIMUM_COVERAGE = 0.8;

    static final double RIDGE_PENALTY = 1.0;

    private final List<SleepFactor> features;
    private final double baseline;
    private final double[] means;
    private final double[] spreads;
    private final double[] coefficients;
    private final int nights;

    private QualityModel(List<SleepFactor> features, double baseline, double[] means, double[] spreads,
                         double[] coefficients, int nights) {
        this.features = features;
        this.baseline = baseline;
        this.means = means;
        this.spreads = spreads;
        this.coefficients = coefficients;
        this.nights = nights;
    }

    /**
     * Fits on the habits recorded on most nights, using only nights where all of them were
     * recorded. Empty when fewer than {@link #MINIMUM_NIGHTS} such nights exist.
     */
    public static Optional<QualityModel> fit(List<SleepLog> history) {
        List<SleepFactor> features = CANDIDATES.stream()
                .filter(f -> history.stream().filter(f::recordedOn).count() >= MINIMUM_COVERAGE * history.size())
                .toList();
        List<SleepLog> logs = history.stream()
                .filter(night -> features.stream().allMatch(f -> f.recordedOn(night)))
                .toList();
        if (features.isEmpty() || logs.size() < MINIMUM_NIGHTS) {
            return Optional.empty();
        }
        int n = logs.size();
        int p = features.size();
        double[] quality = SleepFactor.QUALITY.of(logs);
        double baseline = Statistics.mean(quality);
        double[] means = new double[p];
        double[] spreads = new double[p];
        double[][] z = new double[n][p];
        for (int j = 0; j < p; j++) {
            double[] column = features.get(j).of(logs);
            means[j] = Statistics.mean(column);
            spreads[j] = Statistics.populationStandardDeviation(column);
            for (int i = 0; i < n; i++) {
                // A habit that never varies carries no information; leave it at zero.
                z[i][j] = spreads[j] == 0 ? 0 : (column[i] - means[j]) / spreads[j];
            }
        }
        // Solve (ZᵀZ + λI) β = Zᵀ(y - ȳ).
        double[][] system = new double[p][p + 1];
        for (int a = 0; a < p; a++) {
            for (int b = 0; b < p; b++) {
                double sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += z[i][a] * z[i][b];
                }
                system[a][b] = sum + (a == b ? RIDGE_PENALTY : 0);
            }
            double target = 0;
            for (int i = 0; i < n; i++) {
                target += z[i][a] * (quality[i] - baseline);
            }
            system[a][p] = target;
        }
        return Optional.of(new QualityModel(features, baseline, means, spreads, solve(system), n));
    }

    /**
     * Predicted quality from 1 to 10. Habits missing from {@code tonight} are assumed typical.
     */
    public double predict(Map<SleepFactor, Double> tonight) {
        double prediction = baseline;
        for (int j = 0; j < features.size(); j++) {
            Double value = tonight.get(features.get(j));
            if (value != null && spreads[j] != 0) {
                prediction += coefficients[j] * (value - means[j]) / spreads[j];
            }
        }
        return Math.max(1, Math.min(10, prediction));
    }

    /** Change in predicted quality for a one-standard-deviation increase in each habit. */
    public Map<SleepFactor, Double> effects() {
        Map<SleepFactor, Double> effects = new EnumMap<>(SleepFactor.class);
        for (int j = 0; j < features.size(); j++) {
            effects.put(features.get(j), coefficients[j]);
        }
        return effects;
    }

    /** Nights the model was fitted on. */
    public int nights() {
        return nights;
    }

    /** Habits the model uses. */
    public List<SleepFactor> features() {
        return features;
    }

    /** Gaussian elimination with partial pivoting on an augmented matrix. */
    private static double[] solve(double[][] m) {
        int p = m.length;
        for (int col = 0; col < p; col++) {
            int pivot = col;
            for (int row = col + 1; row < p; row++) {
                if (Math.abs(m[row][col]) > Math.abs(m[pivot][col])) {
                    pivot = row;
                }
            }
            double[] swap = m[col];
            m[col] = m[pivot];
            m[pivot] = swap;
            for (int row = col + 1; row < p; row++) {
                double factor = m[row][col] / m[col][col];
                for (int k = col; k <= p; k++) {
                    m[row][k] -= factor * m[col][k];
                }
            }
        }
        double[] x = new double[p];
        for (int row = p - 1; row >= 0; row--) {
            double sum = m[row][p];
            for (int k = row + 1; k < p; k++) {
                sum -= m[row][k] * x[k];
            }
            x[row] = sum / m[row][row];
        }
        return x;
    }
}
