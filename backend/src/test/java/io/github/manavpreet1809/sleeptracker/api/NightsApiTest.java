package io.github.manavpreet1809.sleeptracker.api;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.manavpreet1809.sleeptracker.TestcontainersConfiguration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class NightsApiTest {

    @Autowired
    MockMvc mvc;

    private final String base = "/api/workspaces/" + UUID.randomUUID();

    private static String night(String date, int quality) {
        return """
                {"date": "%s", "quality": %d, "bedtime": "23:15", "wakeTime": "07:00", "sleepHours": 7.5,
                 "screenTimeHours": 3, "waterIntakeMl": 2000}""".formatted(date, quality);
    }

    private ResultActions send(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
                               String body) throws Exception {
        return mvc.perform(request.contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void recordsListsUpdatesAndDeletesANight() throws Exception {
        send(post(base + "/nights"), night("2026-10-01", 7))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", base + "/nights/2026-10-01"))
                .andExpect(jsonPath("$.quality").value(7))
                .andExpect(jsonPath("$.bedtime").value("23:15:00"))
                .andExpect(jsonPath("$.cardioMinutes").value(nullValue()))
                .andExpect(jsonPath("$.source").value("manual"));

        send(put(base + "/nights/2026-10-01"), night("2026-10-01", 4)).andExpect(status().isOk());
        mvc.perform(get(base + "/nights"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quality").value(4));

        mvc.perform(delete(base + "/nights/2026-10-01")).andExpect(status().isNoContent());
        mvc.perform(get(base + "/nights/2026-10-01")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No night recorded for 2026-10-01"));
    }

    @Test
    void secondNightOnTheSameDateConflicts() throws Exception {
        send(post(base + "/nights"), night("2026-10-02", 7)).andExpect(status().isCreated());
        send(post(base + "/nights"), night("2026-10-02", 8)).andExpect(status().isConflict());
    }

    @Test
    void invalidValuesAreReportedPerField() throws Exception {
        send(post(base + "/nights"), night("2026-10-03", 11))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.quality").value("must be less than or equal to 10"));
    }

    @Test
    void pathAndBodyDatesMustMatch() throws Exception {
        send(put(base + "/nights/2026-10-04"), night("2026-10-05", 7)).andExpect(status().isBadRequest());
    }

    @Test
    void workspacesAreIsolated() throws Exception {
        send(post(base + "/nights"), night("2026-10-06", 7)).andExpect(status().isCreated());
        mvc.perform(get("/api/workspaces/" + UUID.randomUUID() + "/nights")).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void invalidWorkspaceIdIsRejected() throws Exception {
        mvc.perform(get("/api/workspaces/not-a-uuid/nights")).andExpect(status().isBadRequest());
    }

    @Test
    void filtersByDateRange() throws Exception {
        for (String date : new String[]{"2026-09-01", "2026-09-15", "2026-09-30"}) {
            send(post(base + "/nights"), night(date, 6)).andExpect(status().isCreated());
        }
        mvc.perform(get(base + "/nights?from=2026-09-10&to=2026-09-20"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date").value("2026-09-15"));
    }

    @Test
    void emptyWorkspaceHasEmptyAnalysis() throws Exception {
        mvc.perform(get(base + "/analysis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nights").value(0))
                .andExpect(jsonPath("$.summary").value(nullValue()));
    }

    @Test
    void demoDataProducesFullAnalysisAndPredictions() throws Exception {
        mvc.perform(post(base + "/demo")).andExpect(jsonPath("$.loaded").value(60));
        mvc.perform(get(base + "/analysis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nights").value(60))
                .andExpect(jsonPath("$.consistency.overall").isNumber())
                .andExpect(jsonPath("$.qualityHabits", hasSize(6)))
                .andExpect(jsonPath("$.model.nights").value(60))
                .andExpect(jsonPath("$.trend", hasSize(60)));

        // The demo data is built so that screens hurt sleep; the model should find that.
        send(post(base + "/predictions"), "{\"screenTimeHours\": 1}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basedOnNights").value(60));
        String few = mvc.perform(post(base + "/predictions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"screenTimeHours\": 1}")).andReturn().getResponse().getContentAsString();
        String many = mvc.perform(post(base + "/predictions").contentType(MediaType.APPLICATION_JSON)
                .content("{\"screenTimeHours\": 8}")).andReturn().getResponse().getContentAsString();
        double fewScreens = Double.parseDouble(few.replaceAll(".*\"predictedQuality\":([0-9.]+).*", "$1"));
        double manyScreens = Double.parseDouble(many.replaceAll(".*\"predictedQuality\":([0-9.]+).*", "$1"));
        org.junit.jupiter.api.Assertions.assertTrue(fewScreens > manyScreens);
    }

    @Test
    void predictionNeedsEnoughNights() throws Exception {
        send(post(base + "/nights"), night("2026-10-07", 7)).andExpect(status().isCreated());
        send(post(base + "/predictions"), "{}").andExpect(status().isUnprocessableContent());
    }

    @Test
    void clearingRemovesEveryNight() throws Exception {
        mvc.perform(post(base + "/demo"));
        mvc.perform(delete(base + "/nights")).andExpect(jsonPath("$.deleted").value(60));
        mvc.perform(get(base + "/nights")).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void apiDocsAndHealthAreServed() throws Exception {
        mvc.perform(get("/api/openapi")).andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Sleep Tracker API"));
        mvc.perform(get("/actuator/health")).andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void averageBedtimeCrossesMidnight() throws Exception {
        send(post(base + "/nights"), night("2026-08-01", 7).replace("23:15", "23:30")).andExpect(status().isCreated());
        send(post(base + "/nights"), night("2026-08-02", 7).replace("23:15", "00:30")).andExpect(status().isCreated());
        mvc.perform(get(base + "/analysis"))
                .andExpect(jsonPath("$.summary.bedtime").value("00:00:00"))
                .andExpect(jsonPath("$.summary.averages[?(@.key=='QUALITY')].value").value(org.hamcrest.Matchers.contains(closeTo(7.0, 1e-9))));
    }

    private MockMultipartFile garminSample() throws Exception {
        try (var in = getClass().getResourceAsStream("/garmin-sleep-sample.csv")) {
            return new MockMultipartFile("file", "sleep.csv", "text/csv", in.readAllBytes());
        }
    }

    @Test
    void garminImportAddsNightsAndKeepsHabitsAlreadyLogged() throws Exception {
        // A hand-logged night on the same date as one Garmin row.
        send(post(base + "/nights"), night("2026-03-08", 3)).andExpect(status().isCreated());
        mvc.perform(multipart(base + "/imports/garmin").file(garminSample()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.added").value(3))
                .andExpect(jsonPath("$.updated").value(1))
                .andExpect(jsonPath("$.skipped", hasSize(2)));
        mvc.perform(get(base + "/nights/2026-03-08"))
                .andExpect(jsonPath("$.quality").value(8))
                .andExpect(jsonPath("$.screenTimeHours").value(3.0))
                .andExpect(jsonPath("$.restingHeartRate").value(52))
                .andExpect(jsonPath("$.source").value("garmin"));

        // Editing by hand keeps the heart-rate data the form doesn't show.
        send(put(base + "/nights/2026-03-08"), night("2026-03-08", 9)).andExpect(status().isOk());
        mvc.perform(get(base + "/nights/2026-03-08"))
                .andExpect(jsonPath("$.quality").value(9))
                .andExpect(jsonPath("$.hrvMs").value(61));
    }

    @Test
    void garminImportRejectsOtherFiles() throws Exception {
        mvc.perform(multipart(base + "/imports/garmin")
                        .file(new MockMultipartFile("file", "steps.csv", "text/csv", "Date,Steps\n".getBytes())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("This doesn't look like a Garmin sleep export: no \"Score\" column"));
    }
}
