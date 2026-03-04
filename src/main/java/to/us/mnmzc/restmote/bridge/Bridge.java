package to.us.mnmzc.restmote.bridge;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * JPA entity representing a bridge. Has an ID, name, auth token, and its list of filter rules.
 */
@Entity
public class Bridge {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String authToken;
}
