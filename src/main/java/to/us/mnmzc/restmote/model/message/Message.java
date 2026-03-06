package to.us.mnmzc.restmote.model.message;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;
import to.us.mnmzc.restmote.model.message.filter.Filter;
import to.us.mnmzc.restmote.model.transmitter.Transmitter;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a message send by a transmitter to a receiver.
 * Contains the payload, timestamp, attributes, and receiver filter.
 */
@Builder
@Getter
@ToString
public class Message {
    private final Transmitter source;
    private final MessagePayloadType payloadType;
    private final JsonNode payload;
    @Builder.Default
    private final Instant timestamp = Instant.now();
    private final Map<String, Object> attributes;
    @Nullable private final Filter receiverFilter;
}
