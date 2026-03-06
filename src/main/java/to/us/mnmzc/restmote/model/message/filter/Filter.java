package to.us.mnmzc.restmote.model.message.filter;

import lombok.*;

import java.util.List;

/**
 * Represents a filter rule. This is used to determine where messages should be routed to.
 * Messages may pass optional attributes. Each bridge will check its filters against the message attributes.
 * Receivers may also have attributes. Bridges will check the receiver attributes against another set of filters.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class Filter {
    @Getter final private String name;
    @Getter final private List<FilterCondition> conditions;
}
