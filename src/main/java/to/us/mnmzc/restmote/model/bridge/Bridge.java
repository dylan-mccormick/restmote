package to.us.mnmzc.restmote.model.bridge;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import to.us.mnmzc.restmote.model.message.filter.Filter;

import java.util.UUID;

/**
 * JPA entity representing a bridge. Has an ID, name, auth token, and its list of filter rules.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Bridge {
    @Builder.Default
    @Getter final private String id = UUID.randomUUID().toString();
    @Getter final private String name;
    @Getter final private String authToken;

    @Getter @Nullable final private Filter filter;
}
