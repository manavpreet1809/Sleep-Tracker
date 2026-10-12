package io.github.manavpreet1809.sleeptracker.nights;

import io.github.manavpreet1809.sleeptracker.domain.SleepLog;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Stores and retrieves nights, one per date, within a workspace. */
@Service
@Transactional
public class SleepNightService {

    // Open-ended ranges stay within what every database can store.
    private static final LocalDate EARLIEST = LocalDate.of(1900, 1, 1);
    private static final LocalDate LATEST = LocalDate.of(9999, 12, 31);

    private final SleepNightRepository repository;
    private final int maxNightsPerWorkspace;

    public SleepNightService(SleepNightRepository repository,
                             @Value("${sleeptracker.max-nights-per-workspace:1000}") int maxNightsPerWorkspace) {
        this.repository = repository;
        this.maxNightsPerWorkspace = maxNightsPerWorkspace;
    }

    @Transactional(readOnly = true)
    public List<SleepNight> list(UUID workspace, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return repository.findByWorkspaceIdOrderByNightDateAsc(workspace);
        }
        return repository.findByWorkspaceIdAndNightDateBetweenOrderByNightDateAsc(workspace,
                from == null ? EARLIEST : from, to == null ? LATEST : to);
    }

    @Transactional(readOnly = true)
    public List<SleepLog> history(UUID workspace) {
        return repository.findByWorkspaceIdOrderByNightDateAsc(workspace).stream().map(SleepNight::toLog).toList();
    }

    @Transactional(readOnly = true)
    public SleepNight get(UUID workspace, LocalDate date) {
        return repository.findByWorkspaceIdAndNightDate(workspace, date)
                .orElseThrow(() -> new NightNotFoundException(date));
    }

    public SleepNight create(UUID workspace, SleepLog night) {
        if (repository.existsByWorkspaceIdAndNightDate(workspace, night.date())) {
            throw new NightAlreadyExistsException(night.date());
        }
        requireRoom(workspace, 1);
        return repository.save(new SleepNight(workspace, night, NightSource.MANUAL));
    }

    /** Updates the night if it exists, otherwise records it. */
    public SleepNight put(UUID workspace, SleepLog night) {
        return repository.findByWorkspaceIdAndNightDate(workspace, night.date())
                .map(existing -> {
                    existing.apply(night, NightSource.MANUAL);
                    return existing;
                })
                .orElseGet(() -> create(workspace, night));
    }

    public void delete(UUID workspace, LocalDate date) {
        repository.delete(get(workspace, date));
    }

    /** Replaces every night in the workspace, e.g. with demo data. */
    public int replaceAll(UUID workspace, List<SleepLog> nights, NightSource source) {
        if (nights.size() > maxNightsPerWorkspace) {
            throw new WorkspaceFullException(maxNightsPerWorkspace);
        }
        repository.deleteAllInWorkspace(workspace);
        repository.flush();
        repository.saveAll(nights.stream().map(night -> new SleepNight(workspace, night, source)).toList());
        return nights.size();
    }

    public int clear(UUID workspace) {
        return repository.deleteAllInWorkspace(workspace);
    }

    private void requireRoom(UUID workspace, int adding) {
        if (repository.countByWorkspaceId(workspace) + adding > maxNightsPerWorkspace) {
            throw new WorkspaceFullException(maxNightsPerWorkspace);
        }
    }
}
