package to.us.mnmzc.restmote.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a filter configuration entry. This is used to generate a filter from the application
 * properties.
 */
@Getter
@Setter
public class FilterConfigEntry {
  private String name;
  private List<FilterConditionConfigEntry> conditions;
}
