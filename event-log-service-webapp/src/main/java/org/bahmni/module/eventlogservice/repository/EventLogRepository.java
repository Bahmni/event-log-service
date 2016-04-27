package org.bahmni.module.eventlogservice.repository;

import org.bahmni.module.eventlogservice.model.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EventLogRepository extends JpaRepository<EventLog, Integer> {
    EventLog findFirstByOrderByTimestampDesc();

    List<EventLog> findTop100ByFilterStartingWithAndIdAfter(@Param("filter") String filter, @Param("id") Integer id);

    List<EventLog> findTop100ByFilterStartingWith(@Param("filter") String filter);

    List<EventLog> findTop100ByCategoryIs(@Param("category") String category);

    List<EventLog> findTop100ByCategoryIsAndIdAfter(@Param("category") String category, @Param("id") Integer id);

    EventLog findByUuid(@Param("uuid") String uuid);

    EventLog findFirstByOrderByIdDesc();
}
