package to.us.mnmzc.restmote.api.v1.receiver;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import to.us.mnmzc.restmote.model.message.filter.Filter;
import to.us.mnmzc.restmote.model.receiver.ReceiverRegistry;
import to.us.mnmzc.restmote.model.receiver.ReceiverSession;
import to.us.mnmzc.restmote.model.receiver.strategy.WebSocketStrategy;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A WebSocket handler for assigning websocket sessions to receiver sessions.
 */
@Component
@NullMarked
@Slf4j
public class WebSocketReceiverHandler extends TextWebSocketHandler {
    @Autowired private ReceiverRegistry registry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parses the query parameters of the WebSocket URI to extract any relevant information for the receiver session.
     * @return A map of query parameter names to values.
     */
    private Map<String, String> parseWsUriQuery(URI uri) {
        String query = uri.getQuery();
        if (query == null || query.isEmpty()) {
            return Map.of();
        }

        return Arrays.stream(query.split("&"))
                .map(part -> part.split("=", 2))
                .filter(p -> p.length == 2 && !p[0].isBlank())
                .map(p -> new AbstractMap.SimpleEntry<>(p[0], p[1]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
    }

    /**
     * Ensures "attributes" is either:
     * 1) a JSON object node, or
     * 2) a JSON string that parses into an object node.
     * @param attributesNode the JsonNode representing the "attributes" field in the incoming WebSocket message
     * @return a Map<String, Object> parsed from the "attributes" field
     * @throws JacksonException if the "attributes" string cannot be parsed into a valid JSON object or if the JSON object cannot be converted into a Map<String, Object>
     */
    private Map<String, Object> extractAttributesMap(JsonNode attributesNode) throws JacksonException {
        if (attributesNode.isNull()) {
            throw new IllegalArgumentException("'attributes' cannot be null");
        }

        JsonNode objectNode;
        if (attributesNode.isObject()) {
            objectNode = attributesNode;
        } else if (attributesNode.isTextual()) {
            String raw = attributesNode.asText();
            if (raw.isBlank()) {
                throw new IllegalArgumentException("'attributes' string cannot be blank");
            }
            objectNode = objectMapper.readTree(raw);
            if (objectNode == null || !objectNode.isObject()) {
                throw new IllegalArgumentException("'attributes' string must contain a valid JSON object");
            }
        } else {
            throw new IllegalArgumentException("'attributes' must be a JSON object or a JSON string containing an object");
        }

        return objectMapper.convertValue(objectNode, new TypeReference<>() {});
    }

    /**
     * Ensures "filter" is either:
     * 1) a JSON object node, or
     * 2) a JSON string that parses into an object node.
     * @param filterNode the JsonNode representing the "filter" field in the incoming WebSocket message
     * @return a Filter object parsed from the "filter" field
     * @throws JacksonException if the "filter" string cannot be parsed into a valid JSON object or if the JSON object cannot be converted into a Filter instance
     */
    private Filter extractFilter(JsonNode filterNode) throws JacksonException {
        if (filterNode.isNull()) {
            throw new IllegalArgumentException("'filter' cannot be null");
        }

        JsonNode objectNode;
        if (filterNode.isObject()) {
            objectNode = filterNode;
        } else if (filterNode.isTextual()) {
            String raw = filterNode.asText();
            if (raw.isBlank()) {
                throw new IllegalArgumentException("'filter' string cannot be blank");
            }
            objectNode = objectMapper.readTree(raw);
            if (objectNode == null || !objectNode.isObject()) {
                throw new IllegalArgumentException("'filter' string must contain a valid JSON object");
            }
        } else {
            throw new IllegalArgumentException("'filter' must be a JSON object or a JSON string containing an object");
        }

        return objectMapper.treeToValue(objectNode, Filter.class);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        ReceiverSession receiverSession = registry.getSessionById(session.getId())
                .orElseThrow(() -> new IllegalStateException("Received message for unregistered WebSocket session: " + session.getId()));
        JsonNode json;
        Map<String, Object> attributes = null;
        Filter filter = null;

        try {
            json = objectMapper.readTree(message.getPayload());

            if (!json.has("filter") && !json.has("attributes")) {
                throw new IllegalArgumentException("WebSocket message must contain either 'filter' or 'attributes' field");
            }

            if (json.has("attributes")) {
                attributes = extractAttributesMap(json.get("attributes"));
            }

            if (json.has("filter")) {
                filter = extractFilter(json.get("filter"));
            }
        } catch (JacksonException | IllegalArgumentException e) {
            log.error("Failed to parse incoming WebSocket message as valid WS update JSON", e);
            return;
        }

        // update attributes
        if (attributes != null) {
            log.info("Updating attributes for WebSocket session {}: {}", session.getId(), attributes);
            receiverSession.setAttributes(attributes);
        }

        // update filter
        if (filter != null) {
            log.info("Updating filter for WebSocket session {}: {}", session.getId(), filter);
            receiverSession.setFilter(filter);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        registry.unregister(session.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        if (session.getUri() == null) {
            log.error("WebSocket session has no URI, cannot establish receiver session");
            session.close();
            return;
        }
        Map<String, String> queryParams = parseWsUriQuery(session.getUri());
        WebSocketStrategy strategy = new WebSocketStrategy(session);
        ReceiverSession.ReceiverSessionBuilder builder = ReceiverSession.builder()
                .id(session.getId())
                .strategy(strategy);

        // try to parse bridgeIds, attributes, and filters
        if (queryParams.containsKey("bridgeIds")) {
            List<String> ids = Arrays.stream(queryParams.get("bridgeIds").split(",")).toList();
            builder.bridgeIds(ids);
        }

        registry.register(builder.build());
    }
}
