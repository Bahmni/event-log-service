package org.bahmni.module.eventlogservice.repository;

import org.bahmni.module.eventlogservice.model.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EventLogRepository extends JpaRepository<EventLog, Integer> {
    EventLog findFirstByOrderByTimestampDesc();

    List<EventLog> findTop100ByFilterInAndIdAfterAndCategoryNotIn(@Param("filter") List<String> filter, @Param("id") Integer id,
                                                                  @Param("category") List<String> categoryList);

    List<EventLog> findTop100ByFilterContainsAndIdAfterAndCategoryNotIn(@Param("filter") String filter, @Param("id") Integer id,
                                                                  @Param("category") List<String> categoryList);

    List<EventLog> findTop100ByFilterInAndCategoryNotIn(@Param("filter") List<String> filter, @Param("category") List<String> categoryList);

    List<EventLog> findTop100ByFilterContainsAndCategoryNotIn(@Param("filter") String filter, @Param("category") List<String> categoryList);

    List<EventLog> findTop100ByCategoryAndFilterIsNull(@Param("category") String category);

    List<EventLog> findTop100ByCategoryAndIdAfterAndFilterIsNull(@Param("category") String category, @Param("id") Integer id);

    List<EventLog> findTop100ByCategoryAndFilterStartingWith(@Param("category") String category, @Param("filter") String filter);

    List<EventLog> findTop100ByCategoryAndFilterStartingWithAndIdAfter(@Param("category") String category,
                                                                       @Param("filter") String filter, @Param("id") Integer id);


    List<EventLog> findTop100ByCategoryIs(@Param("category") String category);

    List<EventLog> findTop100ByCategoryIsAndIdAfter(@Param("category") String category, @Param("id") Integer id);

    EventLog findTop1ByUuid(@Param("uuid") String uuid);

    EventLog findTop1ByCategoryAndObjectOrderByIdDesc(@Param("category") String category,@Param("object") String object);

    EventLog findFirstByOrderByIdDesc();

    Integer countByFilterInAndCategoryNotIn(@Param("filter") List<String> filter, @Param("category") List<String> categoryList);

    Integer countByFilterInAndIdAfterAndCategoryNotIn(@Param("filter") List<String> filter, @Param("id") Integer id,
                                                      @Param("category") List<String> categoryList);

    Integer countByCategoryAndFilterIsNull(@Param("category") String category);

    Integer countByCategoryAndIdAfterAndFilterIsNull(@Param("category") String category, @Param("id") Integer lastReadEventLogId);

    Integer countByCategoryAndFilterStartingWith(@Param("category") String category, @Param("filter") String filter);

    Integer countByCategoryAndFilterStartingWithAndIdAfter(@Param("category") String category, @Param("filter") String filter,  @Param("id") Integer lastReadEventLogId);

    Integer countByCategoryIsAndIdAfter(@Param("category") String category, @Param("id") Integer lastReadEventLogId);

    Integer countByCategoryIs(@Param("category")String category);
}
