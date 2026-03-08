package to.us.mnmzc.restmote.model.message.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

/**
 * Represents a filter rule. This is used to determine where messages should be routed to.
 * Messages may pass optional attributes. Each bridge will check its filters against the message attributes.
 * Receivers may also have attributes. Bridges will check the receiver attributes against another set of filters.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = Filter.FilterBuilder.class)
public class Filter {
    @Getter @JsonProperty("name") final private String name;
    @Getter @JsonProperty("conditions") final private List<FilterCondition> conditions;

    @JsonPOJOBuilder(withPrefix = "")
    public static class FilterBuilder {
    }
}
