package io.github.manavpreet1809.sleeptracker.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StatisticsTest {

    @Test
    void meanOfTwoNights() {
        assertEquals(4.5, Statistics.mean(new double[]{7, 2}));
    }

    @Test
    void meanRejectsNoValues() {
        assertThrows(IllegalArgumentException.class, () -> Statistics.mean(new double[]{}));
    }

    @Test
    void sampleVarianceOfTwoValues() {
        assertEquals(2.0, Statistics.sampleVariance(new double[]{5, 7}), 0.001);
    }

    @Test
    void sampleVarianceOfOneValueIsZero() {
        assertEquals(0, Statistics.sampleVariance(new double[]{5}));
    }

    @Test
    void populationStandardDeviationMatchesOriginalExamples() {
        assertEquals(6, Math.round(Statistics.populationStandardDeviation(new double[]{14, 24, 11})));
        assertEquals(19, Math.round(Statistics.populationStandardDeviation(new double[]{14, 24, 11, 69, 26, 47, 19})));
        assertEquals(4, Math.round(Statistics.populationStandardDeviation(new double[]{-12, -2, -8})));
        assertEquals(33, Math.round(Statistics.populationStandardDeviation(
                new double[]{25.56, 24.24, 17.65, 107.11, 75.25, 69.71})));
    }

    @Test
    void outlierRemovalIgnoresExtremeNight() {
        double[] values = {7, 7.5, 8, 7, 7.5, 8, 7, 7.5, 8, 30};
        double withOutlier = Statistics.sampleStandardDeviation(values, false);
        double withoutOutlier = Statistics.sampleStandardDeviation(values, true);
        assertEquals(0.44, withoutOutlier, 0.01);
        assertTrue(withOutlier > 7);
    }

    @Test
    void correlationOfPerfectlyRelatedSeries() {
        assertEquals(1, Statistics.pearsonCorrelation(new double[]{1, 2, 3}, new double[]{2, 4, 6}), 1e-9);
        assertEquals(-1, Statistics.pearsonCorrelation(new double[]{1, 2, 3}, new double[]{6, 4, 2}), 1e-9);
    }

    @Test
    void correlationWithConstantSeriesIsZero() {
        assertEquals(0, Statistics.pearsonCorrelation(new double[]{1, 2, 3}, new double[]{5, 5, 5}));
    }

    @Test
    void weightedMovingAverageFavoursRecentNights() {
        // Weights 1/3, 2/3, 1 over the last three values 2, 4, 6.
        assertEquals(14.0 / 3, Statistics.weightedMovingAverage(new double[]{100, 2, 4, 6}, 3), 1e-9);
        assertEquals(Statistics.weightedMovingAverage(new double[]{100, 2, 4, 6}, 0),
                Statistics.weightedMovingAverage(new double[]{100, 2, 4, 6}, 10), 1e-9);
    }
}
