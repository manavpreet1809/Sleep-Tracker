package io.github.manavpreet1809.sleeptracker.nights;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SleepNightRepository extends JpaRepository<SleepNight, Long> {

    List<SleepNight> findByWorkspaceIdOrderByNightDateAsc(UUID workspaceId);

    List<SleepNight> findByWorkspaceIdAndNightDateBetweenOrderByNightDateAsc(UUID workspaceId, LocalDate from, LocalDate to);

    Optional<SleepNight> findByWorkspaceIdAndNightDate(UUID workspaceId, LocalDate nightDate);

    boolean existsByWorkspaceIdAndNightDate(UUID workspaceId, LocalDate nightDate);

    long countByWorkspaceId(UUID workspaceId);

    @Modifying
    @Query("delete from SleepNight n where n.workspaceId = :workspaceId")
    int deleteAllInWorkspace(UUID workspaceId);
}
