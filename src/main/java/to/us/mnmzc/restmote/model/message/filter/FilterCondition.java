package to.us.mnmzc.restmote.model.message.filter;

import java.util.Map;

import org.jspecify.annotations.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single condition to be used in a filter rule.
 *
 * @param field the field to be checked in the message attributes
 * @param operator the operator to be used for the comparison
 * @param expected the expected value to be compared against the actual value in the message
 *     attributes
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FilterCondition(
    @JsonProperty(value = "field", required = true) String field,
    @JsonProperty(value = "operator", required = true) FilterOperator operator,
    @JsonProperty(value = "expected", required = true) Object expected) {

  /**
   * Evaluates this filter condition against the given message attributes.
   *
   * @param attributes the message attributes to be evaluated against this filter condition
   * @return true if the filter condition evaluates to true, false otherwise (or if any exception
   *     occurs during evaluation)
   */
  public boolean evaluate(Map<String, Object> attributes) {
    Object actual = attributes.get(field);

    return operator.test(actual, expected);
  }

  @Override
  @NonNull
  public String toString() {
    return String.format("%s %s %s", this.field, this.operator.name(), this.expected);
  }
}
