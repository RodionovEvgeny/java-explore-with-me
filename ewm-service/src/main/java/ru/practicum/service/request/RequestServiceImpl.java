package ru.practicum.service.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.service.event.Event;
import ru.practicum.service.event.EventRepository;
import ru.practicum.service.event.EventStatus;
import ru.practicum.service.exceptions.ConflictException;
import ru.practicum.service.exceptions.EntityNotFoundException;
import ru.practicum.service.user.User;
import ru.practicum.service.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(long userId, long eventId) {
        return requestRepository.findAllByEventInitiatorIdAndEventId(userId, eventId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
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
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ConflictException(String.format("На событие с id=%s не осталось свободных мест.", eventId));
        }

        RequestStatus status;
        if (event.isRequestModeration()) status = RequestStatus.PENDING;
        else status = RequestStatus.CONFIRMED;

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
        User user = validateUserById(userId);
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
                request.setStatus(updateRequest.getStatus());
            }
            result.setRejectedRequests(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
        } else {
            if (event.getParticipantLimit() == 0) {
                for (ParticipationRequest request : requests) {
                    request.setStatus(updateRequest.getStatus());
                }
                result.setRejectedRequests(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
                event.setConfirmedRequests(requests.size());
                eventRepository.save(event);
            } else {
                int availableRequests = event.getParticipantLimit() - event.getConfirmedRequests();
                int confirmedCount = 0;
                List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
                List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

                for (int n = 0; n < availableRequests; n++) {
                    requests.get(n).setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toRequestDto(requests.get(n)));
                    confirmedCount++;
                }
                for (int n = confirmedCount; n < requests.size(); n++) {
                    requests.get(n).setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toRequestDto(requests.get(n)));
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
