package ru.practicum;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // @Column(name = "")
    private String app;
    //  @Column(name = "")
    private String uri;
    //   @Column(name = "")
    private String ip;
    //  @Column(name = "")

    private LocalDateTime timestamp;
}
