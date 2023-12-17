package ru.practicum.service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.request.model.ParticipationRequest;
import ru.practicum.service.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterIdAndEventId(long userId, long eventId);

    List<ParticipationRequest> findAllByRequesterId(long userId);

    List<ParticipationRequest> findAllByEventInitiatorIdAndEventId(long userId, long eventId);

    List<ParticipationRequest> findAllByIdInAndStatus(List<Long> requestIds, RequestStatus requestStatus);

    Long countByEventIdAndStatus(Long eventId, RequestStatus requestStatus);
}
