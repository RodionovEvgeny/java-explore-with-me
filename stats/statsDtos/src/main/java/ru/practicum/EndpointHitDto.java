package ru.practicum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonDeserialize(using = EndpointHitDateDeserializer.class)
public class EndpointHitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    @JsonSerialize(using = EndpointHitDateSerializer.class)
    private LocalDateTime timestamp;
}
