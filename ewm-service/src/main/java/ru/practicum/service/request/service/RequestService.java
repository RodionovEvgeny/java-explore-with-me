package ru.practicum.service.request.service;

import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequests(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    EventRequestStatusUpdateResult confirmRequests(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getUsersRequests(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);
}
