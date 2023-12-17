package ru.practicum.service.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventStatus;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exceptions.ConflictException;
import ru.practicum.service.exceptions.EntityNotFoundException;
import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.dto.RequestMapper;
import ru.practicum.service.request.model.ParticipationRequest;
import ru.practicum.service.request.model.RequestStatus;
import ru.practicum.service.request.repository.RequestRepository;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(long userId, long eventId) {
        return requestRepository.findAllByEventInitiatorIdAndEventId(userId, eventId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUsersRequests(Long userId) {
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = validateEventById(eventId);
        User user = validateUserById(userId);

        if (userId == event.getInitiator().getId()) {
            throw new ConflictException(String.format("Пользователь с id=%s является создателем события", userId));
        }
        if (requestRepository.findAllByRequesterIdAndEventId(userId, eventId).size() > 0) {
            throw new ConflictException(String.format("Пользователь с id=%s уже подал заявку на участие.", userId));
        }
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException(String.format("Событие с id=%s еще не опубликовано.", eventId));
        }
        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException(String.format("На событие с id=%s не осталось свободных мест.", eventId));
        }

        RequestStatus status;
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) status = RequestStatus.CONFIRMED;
        else status = RequestStatus.PENDING;

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(status)
                .build();
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        validateUserById(userId);
        ParticipationRequest request = validateRequestById(requestId);
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult confirmRequests(long userId, long eventId,
                                                          EventRequestStatusUpdateRequest updateRequest) {
        Event event = validateEventById(eventId);
        validateUserById(userId);

        if (userId != event.getInitiator().getId()) {
            throw new ConflictException(String.format("Пользователь с id = %s не создатель события!", userId));
        }

        if (!event.isRequestModeration()) {
            throw new ConflictException("Для данного события подтверждение участия не требуется.");
        }

        List<ParticipationRequest> requests = requestRepository.findAllByIdInAndStatus(
                updateRequest.getRequestIds(), RequestStatus.PENDING);

        if (requests.size() == 0) {
            throw new ConflictException("Подходящие запросы на учистия не найдены.");
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            for (ParticipationRequest request : requests) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
            }
            result.setRejectedRequests(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
        } else {
            if (event.getParticipantLimit() - event.getConfirmedRequests() == 0 &&
                    updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                throw new ConflictException(String.format("На событие с id=%s не осталось свободных мест.", eventId));
            }

            if (event.getParticipantLimit() == 0) {
                for (ParticipationRequest request : requests) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    requestRepository.save(request);
                }
                result.setConfirmedRequests(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
                event.setConfirmedRequests(requests.size());
                eventRepository.save(event);
            } else {
                int availableRequests = event.getParticipantLimit() - event.getConfirmedRequests();
                int confirmedCount = 0;
                List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
                List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

                for (int n = 0; n < availableRequests && n < requests.size(); n++) {
                    requests.get(n).setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toRequestDto(requests.get(n)));
                    requestRepository.save(requests.get(n));
                    confirmedCount++;
                }
                for (int n = confirmedCount + 1; n < requests.size(); n++) {
                    requests.get(n).setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toRequestDto(requests.get(n)));
                    requestRepository.save(requests.get(n));
                }
                result.setRejectedRequests(rejectedRequests);
                result.setConfirmedRequests(confirmedRequests);
                event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
                eventRepository.save(event);
            }
        }
        return result;
    }

    private Event validateEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Событие с id = %s не найдено!", eventId),
                Event.class.getName()));
    }

    private User validateUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }

    private ParticipationRequest validateRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Запрос с id = %s не найден!", requestId),
                ParticipationRequest.class.getName()));
    }
}
