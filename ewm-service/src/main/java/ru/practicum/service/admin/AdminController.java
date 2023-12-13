package ru.practicum.service.admin;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.category.CategoryDto;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.service.compilation.NewCompilationDto;
import ru.practicum.service.event.EventFullDto;
import ru.practicum.service.event.EventService;
import ru.practicum.service.event.EventStatus;
import ru.practicum.service.event.UpdateEventAdminRequest;
import ru.practicum.service.user.UserDto;
import ru.practicum.service.user.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;

    private final CompilationService compilationService;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @PostMapping("/categories")
    public CategoryDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@RequestParam(name = "catId") Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable(name = "catId") Long catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEventsAllInfo(@RequestParam(required = false) List<Long> users,
                                               @RequestParam(required = false) List<EventStatus> states,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        LocalDateTime start;
        LocalDateTime end;

        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, FORMATTER);
        }

        if (rangeEnd == null) {
            end = LocalDateTime.MAX;
        } else {
            end = LocalDateTime.parse(rangeEnd, FORMATTER);
        }

        return eventService.getEventsByAdmin(users, states, categories, start, end, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable(name = "eventId") Long eventId,
                                           @RequestBody UpdateEventAdminRequest updateEvent) {
        return eventService.updateEventByAdmin(eventId, updateEvent);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> users,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsers(users, from, size);
    }

    @PostMapping("/users")
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        return userService.addUser(userDto);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable(name = "compId") Long compId,
                                            @RequestBody @Valid NewCompilationDto compilationDto) {
        return compilationService.updateCompilation(compId, compilationDto);
    }
}
