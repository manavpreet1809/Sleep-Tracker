package io.github.manavpreet1809.sleeptracker.analytics;

import java.util.Arrays;

/** Descriptive statistics over plain arrays of measurements. */
public final class Statistics {

    private Statistics() {
    }

    public static double mean(double[] values) {
        requireNonEmpty(values);
        return Arrays.stream(values).average().orElseThrow();
    }

    /** Sample variance (divides by n - 1); zero when there are fewer than two values. */
    public static double sampleVariance(double[] values) {
        if (values.length < 2) {
            return 0;
        }
        double mean = mean(values);
        return Arrays.stream(values).map(v -> (v - mean) * (v - mean)).sum() / (values.length - 1);
    }

    /** Population standard deviation (divides by n). */
    public static double populationStandardDeviation(double[] values) {
        double mean = mean(values);
        return Math.sqrt(Arrays.stream(values).map(v -> (v - mean) * (v - mean)).sum() / values.length);
    }

    /**
     * Sample standard deviation, optionally ignoring values more than two standard deviations from
     * the mean. Outliers are only removed when at least four values exist and two remain afterwards.
     */
    public static double sampleStandardDeviation(double[] values, boolean removeOutliers) {
        if (values.length < 2) {
            return 0;
        }
        double[] kept = values;
        if (removeOutliers && values.length > 3) {
            double mean = mean(values);
            double spread = populationStandardDeviation(values);
            double[] normal = Arrays.stream(values).filter(v -> Math.abs(v - mean) <= 2 * spread).toArray();
            if (normal.length > 1) {
                kept = normal;
            }
        }
        return Math.sqrt(sampleVariance(kept));
    }

    /** Pearson correlation from -1 to 1; zero when either series is constant. */
    public static double pearsonCorrelation(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Series must have the same length");
        }
        requireNonEmpty(x);
        double meanX = mean(x);
        double meanY = mean(y);
        double covariance = 0;
        double varianceX = 0;
        double varianceY = 0;
        for (int i = 0; i < x.length; i++) {
            covariance += (x[i] - meanX) * (y[i] - meanY);
            varianceX += (x[i] - meanX) * (x[i] - meanX);
            varianceY += (y[i] - meanY) * (y[i] - meanY);
        }
        double denominator = Math.sqrt(varianceX * varianceY);
        return denominator == 0 ? 0 : covariance / denominator;
    }

    /**
     * Average of the most recent values, where newer values count for more: the oldest considered
     * value has weight 1/k and the newest weight 1, for k considered values.
     *
     * @param values     values in chronological order
     * @param recentDays how many of the latest values to consider; 0 or more than available means all
     */
    public static double weightedMovingAverage(double[] values, int recentDays) {
        requireNonEmpty(values);
        int first = recentDays > 0 && recentDays < values.length ? values.length - recentDays : 0;
        int count = values.length - first;
        double weightedSum = 0;
        double totalWeight = 0;
        for (int i = first; i < values.length; i++) {
            double weight = (double) (i - first + 1) / count;
            weightedSum += values[i] * weight;
            totalWeight += weight;
        }
        return weightedSum / totalWeight;
    }

    private static void requireNonEmpty(double[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("At least one value is required");
        }
    }
}
