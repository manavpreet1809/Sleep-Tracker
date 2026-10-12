package io.github.manavpreet1809.sleeptracker.imports;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the sleep CSV exported from Garmin Connect. Columns are found by name, so their order may
 * change. Garmin labels a night by the morning it ended; nights here are labelled by the evening
 * they began, so each date moves back one day.
 */
public final class GarminSleepCsv {

    static final int MAX_ROWS = 2000;

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    private static final Pattern DURATION = Pattern.compile("^(?:(\\d+)h)?\\s*(?:(\\d+)\\s*min)?$");
    private static final String MISSING = "--";

    private GarminSleepCsv() {
    }

    /** One row that could not be imported, numbered from the file's first line. */
    public record Skipped(int line, String reason) {
    }

    public record Result(List<SleepLog> nights, List<Skipped> skipped) {
    }

    public static Result parse(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        String header = reader.readLine();
        if (header == null) {
            throw new IllegalArgumentException("The file is empty");
        }
        Map<String, Integer> columns = columns(split(header.replace("﻿", "")));
        for (String required : List.of("Score", "Duration", "Bedtime", "Wake Time")) {
            if (!columns.containsKey(required)) {
                throw new IllegalArgumentException(
                        "This doesn't look like a Garmin sleep export: no \"" + required + "\" column");
            }
        }
        List<SleepLog> nights = new ArrayList<>();
        List<Skipped> skipped = new ArrayList<>();
        String line;
        int number = 1;
        while ((line = reader.readLine()) != null) {
            number++;
            if (line.isBlank()) {
                continue;
            }
            if (nights.size() + skipped.size() >= MAX_ROWS) {
                throw new IllegalArgumentException("The file has more than " + MAX_ROWS + " nights");
            }
            try {
                nights.add(night(split(line), columns));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                skipped.add(new Skipped(number, e.getMessage()));
            }
        }
        return new Result(nights, skipped);
    }

    private static SleepLog night(List<String> cells, Map<String, Integer> columns) {
        LocalDate wokeOn = LocalDate.parse(cell(cells, 0));
        String score = cell(cells, columns.get("Score"));
        if (score.equals(MISSING) || cell(cells, columns.get("Duration")).equals(MISSING)) {
            throw new IllegalArgumentException("No sleep recorded");
        }
        int sleepScore = Integer.parseInt(score);
        return new SleepLog(
                wokeOn.minusDays(1),
                Math.clamp(Math.round(sleepScore / 10.0), 1, 10),
                LocalTime.parse(cell(cells, columns.get("Bedtime")).toUpperCase(Locale.US), CLOCK),
                LocalTime.parse(cell(cells, columns.get("Wake Time")).toUpperCase(Locale.US), CLOCK),
                hours(cell(cells, columns.get("Duration"))),
                null, null, null, null, null, null,
                sleepScore,
                optionalInt(cells, columns.get("Resting Heart Rate")),
                optionalInt(cells, columns.get("HRV Status")));
    }

    /** "5h 26min", "13min" or "7h" as decimal hours. */
    static double hours(String text) {
        Matcher m = DURATION.matcher(text.trim());
        if (text.isBlank() || !m.matches()) {
            throw new IllegalArgumentException("Unreadable duration \"" + text + "\"");
        }
        int h = m.group(1) == null ? 0 : Integer.parseInt(m.group(1));
        int min = m.group(2) == null ? 0 : Integer.parseInt(m.group(2));
        return h + min / 60.0;
    }

    private static Integer optionalInt(List<String> cells, Integer column) {
        if (column == null) {
            return null;
        }
        String value = cell(cells, column);
        return value.equals(MISSING) || value.isEmpty() ? null : Integer.valueOf(value);
    }

    private static String cell(List<String> cells, int index) {
        if (index >= cells.size()) {
            throw new IllegalArgumentException("Row has too few columns");
        }
        return cells.get(index).trim();
    }

    private static Map<String, Integer> columns(List<String> header) {
        Map<String, Integer> columns = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            columns.putIfAbsent(header.get(i).trim(), i);
        }
        return columns;
    }

    /** Splits one CSV line, honouring double-quoted cells. */
    static List<String> split(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cell.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                cells.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(c);
            }
        }
        cells.add(cell.toString());
        return cells;
    }
}
