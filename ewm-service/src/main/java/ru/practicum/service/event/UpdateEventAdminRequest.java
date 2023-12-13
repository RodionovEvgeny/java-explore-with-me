package ru.practicum.service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.service.location.Location;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    private String description;
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}
