package to.us.mnmzc.restmote.model.transmitter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Represents the transmitter of messages sent by the client.
 * Contains an API key, name, and bridges for which it transmits to.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Transmitter {

    private final String apiKey;
    @Getter private final String name;
    @Getter private final List<String> bridgeIds;

}
