package ru.practicum;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointHitDateDeserializer extends StdDeserializer<EndpointHitDto> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EndpointHitDateDeserializer(Class<EndpointHitDto> t) {
        super(t);
    }

    public EndpointHitDateDeserializer() {
        this(EndpointHitDto.class);
    }

    @Override
    public EndpointHitDto deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Long id = node.get("id") == null ? null
                : node.get("id").asLong();
        String app = node.get("app") == null ? null
                : node.get("app").asText();
        String ip = node.get("ip") == null ? null
                : node.get("ip").asText();
        String uri = node.get("uri") == null ? null
                : node.get("uri").asText();
        LocalDateTime timestamp = node.get("timestamp") == null ? null
                : LocalDateTime.parse(node.get("timestamp").asText(), formatter);
        return new EndpointHitDto(id, app, uri, ip, timestamp);

    }
}
