package io.github.manavpreet1809.sleeptracker.imports;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class GarminSleepCsvTest {

    /** A made-up export in Garmin Connect's format, including its byte-order mark and "--" gaps. */
    private static GarminSleepCsv.Result sample() throws IOException {
        try (var in = GarminSleepCsvTest.class.getResourceAsStream("/garmin-sleep-sample.csv")) {
            return GarminSleepCsv.parse(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }

    @Test
    void readsNightsLabelledByTheEveningTheyBegan() throws IOException {
        SleepLog night = sample().nights().getFirst();
        assertEquals(LocalDate.of(2026, 3, 8), night.date()); // Garmin's row is the 9th, the morning after
        assertEquals(8, night.quality());                       // score 81 → 8/10
        assertEquals(81, night.sleepScore());
        assertEquals(LocalTime.of(23, 20), night.bedtime());
        assertEquals(LocalTime.of(7, 15), night.wakeTime());
        assertEquals(7.7, night.sleepHours(), 1e-9);
        assertEquals(52, night.restingHeartRate());
        assertEquals(61, night.hrvMs());
        assertNull(night.cardioMinutes());
    }

    @Test
    void handlesShortNightsAndMissingHeartData() throws IOException {
        List<SleepLog> nights = sample().nights();
        assertEquals(4, nights.size());
        assertEquals(13 / 60.0, nights.get(2).sleepHours(), 1e-9);
        assertEquals(4, nights.get(2).quality());
        assertNull(nights.get(3).restingHeartRate());
        assertNull(nights.get(3).hrvMs());
    }

    @Test
    void reportsRowsItCannotRead() throws IOException {
        List<GarminSleepCsv.Skipped> skipped = sample().skipped();
        assertEquals(2, skipped.size());
        assertEquals(new GarminSleepCsv.Skipped(2, "No sleep recorded"), skipped.get(0));
        assertEquals(new GarminSleepCsv.Skipped(7, "Unreadable duration \"soon\""), skipped.get(1));
    }

    @Test
    void rejectsOtherFiles() {
        var error = assertThrows(IllegalArgumentException.class,
                () -> GarminSleepCsv.parse(new StringReader("Date,Steps\n2026-03-09,9000\n")));
        assertEquals("This doesn't look like a Garmin sleep export: no \"Score\" column", error.getMessage());
        assertThrows(IllegalArgumentException.class, () -> GarminSleepCsv.parse(new StringReader("")));
    }

    @Test
    void parsesDurationsAndQuotedCells() {
        assertEquals(7.5, GarminSleepCsv.hours("7h 30min"), 1e-9);
        assertEquals(7, GarminSleepCsv.hours("7h"), 1e-9);
        assertEquals(List.of("a", "b,c", "say \"hi\""), GarminSleepCsv.split("a,\"b,c\",\"say \"\"hi\"\"\""));
    }
}
