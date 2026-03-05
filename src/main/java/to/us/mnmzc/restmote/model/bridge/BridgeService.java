package to.us.mnmzc.restmote.model.bridge;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import to.us.mnmzc.restmote.config.BridgeConfig;
import to.us.mnmzc.restmote.config.BridgeConfigEntry;
import to.us.mnmzc.restmote.config.FilterConditionConfigEntry;
import to.us.mnmzc.restmote.config.FilterConfigEntry;
import to.us.mnmzc.restmote.model.message.filter.Filter;
import to.us.mnmzc.restmote.model.message.filter.FilterCondition;

import java.util.List;

/**
 * This service is responsible for managing bridges.
 * Holds a list of bridges, and also returns the bridge which matches the provided bridge token.
 */
@Service
@Slf4j
public class BridgeService {

    private final List<Bridge> bridges;

    /**
     * Gets the bridge with the given auth token. This is used to determine which bridge a message should be routed to.
     * @param ids The list of bridge IDs to get.
     * @return The list of bridges with the given IDs, or an empty list if no bridge matches the given IDs.
     */
    public List<Bridge> getBridgesByIds(List<String> ids) {
        return this.bridges.stream().filter(bridge -> ids.contains(bridge.getId())).toList();
    }

    /**
     * Initializes the bridge service with the provided configuration. The configuration is used to generate the list of bridges.
     * @param config The configuration to use for generating the list of bridges.
     */
    private BridgeService(BridgeConfig config) {
        this.bridges = config.getBridgeEntries().stream().map(this::toBridge).toList();
        log.info("Initialized BridgeService with {} bridges", this.bridges.size());
    }

    /**
     * Gets the bridge with the given auth token. This is used to determine which bridge a message should be routed to.
     * @param entry The bridge configuration entry to convert to a bridge.
     * @return The bridge with the given auth token, or null if no bridge matches the given auth token.
     */
    private Bridge toBridge(BridgeConfigEntry entry) {
        return Bridge.builder()
                .id(entry.getId())
                .name(entry.getName())
                .authToken(entry.getAuthToken())
                .filter(entry.getFilter() == null ? null : toFilter(entry.getFilter()))
                .build();
    }

    /**
     * Converts a filter configuration entry to a filter. This is used to generate the filter for a bridge from the application properties.
     * @param entry The filter configuration entry to convert to a filter.
     * @return The filter generated from the given filter configuration entry.
     */
    private Filter toFilter(FilterConfigEntry entry) {
        return Filter.builder()
                .name(entry.getName())
                .conditions(entry.getConditions().stream().map(this::toFilterCondition).toList())
                .build();
    }

    /**
     * Converts a filter condition configuration entry to a filter condition. This is used to generate the filter conditions for a bridge from the application properties.
     * @param entry The filter condition configuration entry to convert to a filter condition.
     * @return The filter condition generated from the given filter condition configuration entry.
     */
    private FilterCondition toFilterCondition(FilterConditionConfigEntry entry) {
        return new FilterCondition(entry.getField(), entry.getOperator(), entry.getValue());
    }

}
