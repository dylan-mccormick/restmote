package to.us.mnmzc.restmote.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/* Helper Classes for generating bridges, filters, and filter conditions. */

/**
 * Represents the bridge configuration. This is used to generate bridges from the application properties.
 */
@Configuration
@ConfigurationProperties(prefix = "restmote")
@Getter
@Setter
public class BridgeConfig {
    private List<BridgeConfigEntry> bridgeEntries;
}