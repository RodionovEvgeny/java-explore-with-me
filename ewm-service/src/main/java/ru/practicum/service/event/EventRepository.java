package ru.practicum.service.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT e " +
            "FROM Event AS e " +
            "WHERE " +
            "(e.state = 'PUBLISHED') AND " +
            "(:text is null or (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))) AND " +
            "(e.category.id IN :categories OR :categories is null) AND " +
            "(e.paid = :paid OR :paid is null) AND " +
            "(e.eventDate BETWEEN :rangeStart AND :rangeEnd) AND " +
            "(:onlyAvailable = false OR (e.confirmedRequests < e.participantLimit))")
    List<Event> getAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                             LocalDateTime rangeEnd, boolean onlyAvailable, Pageable pageable);

    @Query(value = "SELECT e " +
            "FROM Event AS e " +
            "WHERE " +
            "(e.initiator.id IN :users OR :users is null) AND " +
            "(e.state IN :states OR :states is null) AND " +
            "(e.category.id IN :categories OR :categories is null) AND " +
            "(e.eventDate BETWEEN :rangeStart AND :rangeEnd)")
    List<Event> getAllEventsByAdmin(List<Long> users, List<EventStatus> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);


    List<Event> findAllByIdIn(List<Long> events);

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Long countByCategoryId(Long catId);

}
