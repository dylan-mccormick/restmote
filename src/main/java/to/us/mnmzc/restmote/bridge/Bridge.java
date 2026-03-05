package to.us.mnmzc.restmote.bridge;

import jakarta.persistence.*;
import lombok.Getter;
import to.us.mnmzc.restmote.message.filter.Filter;

import java.util.List;

/**
 * JPA entity representing a bridge. Has an ID, name, auth token, and its list of filter rules.
 */
@Entity
public class Bridge {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Getter private String name;
    @Getter private String authToken;

    @ElementCollection
    @Getter private List<String> receiverTokens;

    @Getter private List<Filter> filters;
}
