package io.github.manavpreet1809.sleeptracker.analytics;

import static io.github.manavpreet1809.sleeptracker.analytics.Nights.night;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import java.util.List;
import org.junit.jupiter.api.Test;

class HabitImpactTest {

    private static HabitImpact find(List<HabitImpact> impacts, Habit habit) {
        return impacts.stream().filter(i -> i.habit() == habit).findFirst().orElseThrow();
    }

    @Test
    void comparesNightsWithAndWithoutTheHabit() {
        List<HabitImpact> impacts = HabitImpact.of(List.of(
                night().cardio(90).quality(9).log(),
                night().cardio(70).quality(7).log(),
                night().cardio(0).quality(4).log()), SleepFactor.QUALITY);
        HabitImpact cardio = find(impacts, Habit.CARDIO);
        assertEquals(2, cardio.nightsWith());
        assertEquals(1, cardio.nightsWithout());
        assertEquals(8.0, cardio.averageWith());
        assertEquals(4.0, cardio.averageWithout());
        assertEquals(4.0, cardio.difference());
    }

    @Test
    void goodNightsDoNotCountForUnrelatedHabits() {
        // The original app credited every good night to every habit. Here screens differ only
        // on nights of equal quality, so they show no difference.
        List<HabitImpact> impacts = HabitImpact.of(List.of(
                night().screenTime(1.0).quality(8).log(),
                night().screenTime(6.0).quality(8).log()), SleepFactor.QUALITY);
        assertEquals(0.0, find(impacts, Habit.LIMITED_SCREENS).difference());
    }

    @Test
    void habitNeverBrokenHasNoComparison() {
        List<HabitImpact> impacts = HabitImpact.of(List.of(night().log(), night().log()), SleepFactor.QUALITY);
        HabitImpact water = find(impacts, Habit.HYDRATED);
        assertNull(water.averageWithout());
        assertNull(water.difference());
        assertNull(impacts.getLast().difference());
    }

    @Test
    void largestDifferenceComesFirst() {
        List<HabitImpact> impacts = HabitImpact.of(List.of(
                night().water(3000).cardio(90).quality(9).log(),
                night().water(500).cardio(80).quality(3).log(),
                night().water(2500).cardio(0).quality(8).log()), SleepFactor.QUALITY);
        assertEquals(Habit.HYDRATED, impacts.getFirst().habit());
    }

    @Test
    void durationOutcomeSkipsTheDurationHabit() {
        List<HabitImpact> impacts = HabitImpact.of(List.of(night().log()), SleepFactor.SLEEP_DURATION);
        assertTrue(impacts.stream().noneMatch(i -> i.habit() == Habit.FULL_NIGHT));
    }

    @Test
    void nightsWithoutTheMeasurementAreLeftOut() {
        List<HabitImpact> impacts = HabitImpact.of(List.of(
                night().water(3000).quality(9).log(),
                night().water(null).quality(2).log(),
                night().water(1000).quality(5).log()), SleepFactor.QUALITY);
        HabitImpact water = find(impacts, Habit.HYDRATED);
        assertEquals(1, water.nightsWith());
        assertEquals(1, water.nightsWithout());
        assertEquals(4.0, water.difference());
    }
}
