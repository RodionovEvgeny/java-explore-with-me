package ru.practicum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
@JsonDeserialize(using = EndpointHitDateDeserializer.class)
public class EndpointHitDto {
    private Long id;
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotNull
    @PastOrPresent
    @JsonSerialize(using = EndpointHitDateSerializer.class)
    private LocalDateTime timestamp;
}
