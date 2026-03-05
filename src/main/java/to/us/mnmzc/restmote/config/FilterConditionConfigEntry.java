package to.us.mnmzc.restmote.config;

import lombok.Getter;
import lombok.Setter;
import to.us.mnmzc.restmote.model.message.filter.FilterOperator; /**
 * Represents a filter condition configuration entry. This is used to generate a filter condition from the application properties.
 */
@Getter
@Setter
public class FilterConditionConfigEntry {
    private String field;
    private FilterOperator operator;
    private String value;
}
