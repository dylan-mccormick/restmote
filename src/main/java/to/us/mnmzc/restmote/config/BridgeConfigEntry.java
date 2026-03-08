package to.us.mnmzc.restmote.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a bridge configuration entry. This is used to generate a bridge from the application
 * properties.
 */
@Getter
@Setter
public class BridgeConfigEntry {
  private String id;
  private String name;
  private String authToken;
  private FilterConfigEntry filter;
}
