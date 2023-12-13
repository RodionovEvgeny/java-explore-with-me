package ru.practicum.service.request;

import ru.practicum.service.RequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequests(long userId, long eventId);


    ParticipationRequestDto cancelRequest(long userId, long requestId);

    EventRequestStatusUpdateResult confirmRequests(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getUsersRequests(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);
}
