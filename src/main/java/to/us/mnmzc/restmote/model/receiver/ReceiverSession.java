package to.us.mnmzc.restmote.model.receiver;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.*;
import to.us.mnmzc.restmote.model.message.filter.Filter;
import to.us.mnmzc.restmote.model.receiver.strategy.ReceiverStrategy;

import org.jspecify.annotations.Nullable;

/**
 * Represents a session of a receiver. Used to track the state of a receiver, and how delivery
 * should occur.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class ReceiverSession {
  @Builder.Default @Getter private final String id = UUID.randomUUID().toString();
  @Getter private final List<String> bridgeIds;
  @Setter @Getter private Map<String, Object> attributes;
  @Getter private final ReceiverStrategy strategy;
  @Setter @Getter @Nullable private Filter filter;
}
