package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventDto> getUsersEvents(@RequestParam String text,
                                         @RequestParam List<Integer> categories,
                                         @RequestParam Boolean paid,
                                         @RequestParam String rangeStart,
                                         @RequestParam String rangeEnd,
                                         @RequestParam Boolean onlyAvailable,
                                         @RequestParam SortState sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getUsersEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public EventDto addEvent(@PathVariable Integer userId,
                             @RequestBody @Valid EventDto eventDto) {
        return eventService.addEvent(userId, eventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDtoExtended getEventById(@PathVariable Integer userId,
                                         @PathVariable Integer eventId) {
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto updateEventByUser(@PathVariable(name = "userId") Integer userId,
                                      @PathVariable(name = "eventId") Integer eventId,
                                      @RequestBody @Valid EventDto eventDto) {
        return eventService.updateEventByUser(userId, eventId, eventDto);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable Integer userId,
                                        @PathVariable Integer eventId) {
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestConfirmResponse confirmRequests(@PathVariable Integer userId,
                                                  @PathVariable Integer eventId,
                                                  @RequestBody RequestConfirmRequest requestConfirmRequest) {
        return eventService.confirmRequests(userId, eventId, requestConfirmRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getUsersRequests(@PathVariable Integer userId) {
        return eventService.getUsersRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    public RequestDto addRequest(@PathVariable Integer userId,
                                 @RequestParam Integer eventId) {
        return eventService.addRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Integer userId,
                                    @PathVariable Integer requestId) {
        return eventService.cancelRequest(userId, requestId);
    }
}
