package io.github.manavpreet1809.sleeptracker.api;

import io.github.manavpreet1809.sleeptracker.demo.DemoNights;
import io.github.manavpreet1809.sleeptracker.nights.NightSource;
import io.github.manavpreet1809.sleeptracker.nights.SleepNightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Nights and analysis for one workspace. A workspace is a random UUID chosen by the client, which
 * keeps each visitor's data apart without accounts; it is not a secret or an authentication token.
 */
@RestController
@RequestMapping("/api/workspaces/{workspace}")
@Tag(name = "Sleep tracker")
public class NightsController {

    static final int DEMO_NIGHTS = 60;

    private final SleepNightService nights;
    private final AnalysisService analysis;
    private final Clock clock;

    public NightsController(SleepNightService nights, AnalysisService analysis, Clock clock) {
        this.nights = nights;
        this.analysis = analysis;
        this.clock = clock;
    }

    @GetMapping("/nights")
    @Operation(summary = "List nights, oldest first, optionally between two dates")
    public List<NightResponse> list(@PathVariable UUID workspace,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return nights.list(workspace, from, to).stream().map(NightResponse::of).toList();
    }

    @GetMapping("/nights/{date}")
    @Operation(summary = "Get one night")
    public NightResponse get(@PathVariable UUID workspace,
                             @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return NightResponse.of(nights.get(workspace, date));
    }

    @PostMapping("/nights")
    @Operation(summary = "Record a new night (409 if that date already has one)")
    public ResponseEntity<NightResponse> create(@PathVariable UUID workspace, @Valid @RequestBody NightRequest night) {
        NightResponse created = NightResponse.of(nights.create(workspace, night.toLog()));
        return ResponseEntity.created(URI.create("/api/workspaces/" + workspace + "/nights/" + created.date()))
                .body(created);
    }

    @PutMapping("/nights/{date}")
    @Operation(summary = "Record or replace the night on a date")
    public NightResponse put(@PathVariable UUID workspace,
                             @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @Valid @RequestBody NightRequest night) {
        if (!date.equals(night.date())) {
            throw new IllegalArgumentException("The date in the path and the body must match");
        }
        return NightResponse.of(nights.put(workspace, night.toLog()));
    }

    @DeleteMapping("/nights/{date}")
    @Operation(summary = "Delete a night")
    public ResponseEntity<Void> delete(@PathVariable UUID workspace,
                                       @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        nights.delete(workspace, date);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/nights")
    @Operation(summary = "Delete every night in the workspace")
    public Map<String, Integer> clear(@PathVariable UUID workspace) {
        return Map.of("deleted", nights.clear(workspace));
    }

    @PostMapping("/demo")
    @Operation(summary = "Replace the workspace with 60 nights of made-up example data")
    public Map<String, Integer> loadDemo(@PathVariable UUID workspace) {
        int loaded = nights.replaceAll(workspace,
                DemoNights.generate(LocalDate.now(clock).minusDays(1), DEMO_NIGHTS, workspace.getLeastSignificantBits()),
                NightSource.DEMO);
        return Map.of("loaded", loaded);
    }

    @GetMapping("/analysis")
    @Operation(summary = "Averages, consistency, habit comparisons, sleep-cycle estimate, model and trend")
    public AnalysisResponse analysis(@PathVariable UUID workspace) {
        return analysis.analyse(workspace);
    }

    @PostMapping("/predictions")
    @Operation(summary = "Predict tonight's sleep quality (422 until enough nights are recorded)")
    public ResponseEntity<?> predict(@PathVariable UUID workspace, @Valid @RequestBody PredictionRequest tonight) {
        return analysis.predict(workspace, tonight)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.unprocessableContent().body(ProblemDetail.forStatusAndDetail(
                        HttpStatus.UNPROCESSABLE_CONTENT,
                        "Record at least 7 nights with the same habits tracked before predicting")));
    }
}
