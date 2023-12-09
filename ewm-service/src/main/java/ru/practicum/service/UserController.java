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
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/events")
    public List<EventDto> getUsersEvents(@PathVariable Integer userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsersEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public EventDto addEvent(@PathVariable Integer userId,
                             @RequestBody @Valid EventDto eventDto) {
        return userService.addEvent(userId, eventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDtoExtended getEventById(@PathVariable Integer userId,
                                         @PathVariable Integer eventId) {
        return userService.getEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto updateEventByUser(@PathVariable(name = "userId") Integer userId,
                                      @PathVariable(name = "eventId") Integer eventId,
                                      @RequestBody @Valid EventDto eventDto) {
        return userService.updateEventByUser(userId, eventId, eventDto);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable Integer userId,
                                        @PathVariable Integer eventId) {
        return userService.getRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestConfirmResponse confirmRequests(@PathVariable Integer userId,
                                                  @PathVariable Integer eventId,
                                                  @RequestBody RequestConfirmRequest requestConfirmRequest) {
        return userService.confirmRequests(userId, eventId, requestConfirmRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getUsersRequests(@PathVariable Integer userId) {
        return userService.getUsersRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    public RequestDto addRequest(@PathVariable Integer userId,
                                 @RequestParam Integer eventId) {
        return userService.addRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Integer userId,
                                    @PathVariable Integer requestId) {
        return userService.cancelRequest(userId, requestId);
    }
}
