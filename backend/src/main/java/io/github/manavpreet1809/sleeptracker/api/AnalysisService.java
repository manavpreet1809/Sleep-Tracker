package io.github.manavpreet1809.sleeptracker.api;

import io.github.manavpreet1809.sleeptracker.analytics.ConsistencyReport;
import io.github.manavpreet1809.sleeptracker.analytics.HabitImpact;
import io.github.manavpreet1809.sleeptracker.analytics.QualityModel;
import io.github.manavpreet1809.sleeptracker.analytics.SleepCycleEstimate;
import io.github.manavpreet1809.sleeptracker.analytics.SleepSummary;
import io.github.manavpreet1809.sleeptracker.analytics.Statistics;
import io.github.manavpreet1809.sleeptracker.api.AnalysisResponse.Consistency;
import io.github.manavpreet1809.sleeptracker.api.AnalysisResponse.Cycles;
import io.github.manavpreet1809.sleeptracker.api.AnalysisResponse.Factor;
import io.github.manavpreet1809.sleeptracker.api.AnalysisResponse.HabitResult;
import io.github.manavpreet1809.sleeptracker.api.AnalysisResponse.Model;
import io.github.manavpreet1809.sleeptracker.api.AnalysisResponse.Summary;
import io.github.manavpreet1809.sleeptracker.api.AnalysisResponse.TrendPoint;
import io.github.manavpreet1809.sleeptracker.domain.SleepFactor;
import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import io.github.manavpreet1809.sleeptracker.nights.SleepNightService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** Turns a workspace's history into the dashboard's analysis and predictions. */
@Service
public class AnalysisService {

    static final int TREND_WINDOW = 7;

    private final SleepNightService nights;

    public AnalysisService(SleepNightService nights) {
        this.nights = nights;
    }

    public AnalysisResponse analyse(UUID workspace) {
        List<SleepLog> history = nights.history(workspace);
        if (history.isEmpty()) {
            return new AnalysisResponse(0, null, null, List.of(), List.of(), null, null, List.of());
        }
        SleepSummary summary = SleepSummary.of(history);
        SleepLog last = history.getLast();
        SleepCycleEstimate cycles = SleepCycleEstimate.of(last);
        return new AnalysisResponse(
                history.size(),
                new Summary(factors(summary.averages()), summary.bedtime(), summary.wakeTime()),
                ConsistencyReport.of(history)
                        .map(c -> new Consistency(c.overall(), c.bedtime(), c.wakeTime(), c.duration(), c.rating()))
                        .orElse(null),
                habits(history, SleepFactor.QUALITY),
                habits(history, SleepFactor.SLEEP_DURATION),
                new Cycles(last.date(), cycles.cycles(), cycles.cycleMinutes(), cycles.remMinutes(),
                        cycles.deepSleepMinutes()),
                QualityModel.fit(history).map(m -> new Model(m.nights(), factors(m.effects()))).orElse(null),
                trend(history));
    }

    public Optional<PredictionResponse> predict(UUID workspace, PredictionRequest tonight) {
        return QualityModel.fit(nights.history(workspace))
                .map(model -> new PredictionResponse(model.predict(tonight.toFactors()), model.nights()));
    }

    private static List<Factor> factors(Map<SleepFactor, Double> values) {
        return values.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new Factor(e.getKey().name(), e.getKey().displayName(), e.getKey().unit(), e.getValue()))
                .toList();
    }

    private static List<HabitResult> habits(List<SleepLog> history, SleepFactor outcome) {
        return HabitImpact.of(history, outcome).stream()
                .map(h -> new HabitResult(h.habit().name(), h.habit().description(), h.nightsWith(),
                        h.nightsWithout(), h.averageWith(), h.averageWithout(), h.difference()))
                .toList();
    }

    private static List<TrendPoint> trend(List<SleepLog> history) {
        double[] quality = SleepFactor.QUALITY.of(history);
        double[] hours = SleepFactor.SLEEP_DURATION.of(history);
        List<TrendPoint> points = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            SleepLog night = history.get(i);
            int from = Math.max(0, i + 1 - TREND_WINDOW);
            points.add(new TrendPoint(night.date(), night.quality(), night.sleepHours(), night.bedtime(),
                    night.wakeTime(),
                    Statistics.weightedMovingAverage(Arrays.copyOfRange(quality, from, i + 1), 0),
                    Statistics.weightedMovingAverage(Arrays.copyOfRange(hours, from, i + 1), 0)));
        }
        return points;
    }
}
