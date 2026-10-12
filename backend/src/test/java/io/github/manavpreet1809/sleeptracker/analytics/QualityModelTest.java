package io.github.manavpreet1809.sleeptracker.analytics;

import static io.github.manavpreet1809.sleeptracker.analytics.Nights.night;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class QualityModelTest {

    /** Quality falls by one point per hour of screen time; other habits are constant. */
    private static List<SleepLog> screenTimeHistory() {
        List<SleepLog> nights = new ArrayList<>();
        for (int hours = 1; hours <= 8; hours++) {
            nights.add(night().screenTime(hours).quality(10 - hours).log());
        }
        return nights;
    }

    @Test
    void needsSevenNights() {
        assertTrue(QualityModel.fit(screenTimeHistory().subList(0, 6)).isEmpty());
        assertTrue(QualityModel.fit(screenTimeHistory().subList(0, 7)).isPresent());
    }

    @Test
    void learnsThatMoreScreenTimeMeansWorseSleep() {
        QualityModel model = QualityModel.fit(screenTimeHistory()).orElseThrow();
        double fewScreens = model.predict(Map.of(SleepFactor.SCREEN_TIME, 2.0));
        double manyScreens = model.predict(Map.of(SleepFactor.SCREEN_TIME, 7.0));
        assertTrue(fewScreens > manyScreens);
        assertTrue(model.effects().get(SleepFactor.SCREEN_TIME) < 0);
        assertEquals(0, model.effects().get(SleepFactor.CARDIO), 1e-9);
    }

    @Test
    void ridgePenaltyShrinksTowardsTheAverage() {
        // Exact least squares would predict 8.0 at two hours; the penalty pulls it towards 5.5.
        double prediction = QualityModel.fit(screenTimeHistory()).orElseThrow()
                .predict(Map.of(SleepFactor.SCREEN_TIME, 2.0));
        assertTrue(prediction > 5.5 && prediction < 8.0);
    }

    @Test
    void typicalNightPredictsAverageQuality() {
        QualityModel model = QualityModel.fit(screenTimeHistory()).orElseThrow();
        assertEquals(5.5, model.predict(Map.of()), 1e-9);
        assertEquals(8, model.nights());
    }

    @Test
    void predictionStaysOnTheScale() {
        QualityModel model = QualityModel.fit(screenTimeHistory()).orElseThrow();
        assertEquals(1, model.predict(Map.of(SleepFactor.SCREEN_TIME, 24.0)));
    }
}
