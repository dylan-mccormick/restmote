package to.us.mnmzc.restmote.model.bridge;

import java.util.UUID;

import lombok.*;
import to.us.mnmzc.restmote.model.message.filter.Filter;

import org.jspecify.annotations.Nullable;

/**
 * JPA entity representing a bridge. Has an ID, name, auth token (future), and its list of filter
 * rules.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class Bridge {
  @Builder.Default @Getter private final String id = UUID.randomUUID().toString();
  @Getter private final String name;
  // @Getter private final String authToken;

  @Getter @Nullable private final Filter filter;
}
