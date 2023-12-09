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
import ru.practicum.service.CategoryDto;
import ru.practicum.service.CompilationDto;
import ru.practicum.service.EventDto;
import ru.practicum.service.UserDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/categories")
    public CategoryDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return adminService.addCategory();
    }

    @DeleteMapping("/categories/{catId}")
    public CategoryDto deleteCategory(@RequestParam(name = "catId") Integer catId) {
        return adminService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable(name = "catId") Integer catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        return adminService.updateCategory(catId, categoryDto);
    }

    @GetMapping("/events")
    public List<EventDto> getEventsAllInfo(@RequestParam List<Integer> users,
                                           @RequestParam List<String> states,
                                           @RequestParam List<Integer> categories,
                                           @RequestParam LocalDateTime rangeStart,
                                           @RequestParam LocalDateTime rangeEnd, // TODO string???
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return adminService.getEventsAllInfo(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventDto updateEventByAdmin(@PathVariable(name = "eventId") Integer eventId,
                                       @RequestBody @Valid EventDto eventDto) {
        return adminService.updateEventByAdmin(eventId, eventDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam List<Integer> users,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        return adminService.getUsers(users, from, size);
    }

    @PostMapping("/users")
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        return adminService.addUser(userDto);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        return adminService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody @Valid CompilationDto compilationDto) {
        return adminService.addCompilation(compilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable Integer compId) {
        return adminService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable(name = "compId") Integer compId,
                                            @RequestBody @Valid CompilationDto compilationDto) {
        return adminService.updateCompilation(compId, compilationDto);
    }


}
