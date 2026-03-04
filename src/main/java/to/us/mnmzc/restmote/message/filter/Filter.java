package to.us.mnmzc.restmote.message.filter;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Represents a filter rule. This is used to determine where messages should be routed to.
 * Messages may pass optional attributes. Each bridge will check its filters against the message attributes.
 * Receivers may also have attributes. Bridges will check the receiver attributes against another set of filters.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Filter {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Getter private String id;
    @Getter private String name;
    @Embedded
    @Getter private List<FilterCondition> conditions;
}
