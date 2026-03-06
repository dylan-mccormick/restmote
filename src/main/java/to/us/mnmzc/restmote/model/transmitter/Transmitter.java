package to.us.mnmzc.restmote.model.transmitter;

import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Represents the transmitter of messages sent by the client.
 * Contains an API key (future), name, and bridges for which it transmits to.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class Transmitter {

    // private final String apiKey;
    @Builder.Default
    @Getter private final String id = UUID.randomUUID().toString();
    @Getter private final String name;
    @Getter private final List<String> bridgeIds;

}
