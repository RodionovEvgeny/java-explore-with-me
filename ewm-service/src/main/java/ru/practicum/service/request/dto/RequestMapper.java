package ru.practicum.service.request.dto;

import ru.practicum.service.request.model.ParticipationRequest;

public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }


}
