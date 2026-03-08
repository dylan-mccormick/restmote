package to.us.mnmzc.restmote.api.v1.transmitter;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import to.us.mnmzc.restmote.api.v1.error.ApiMessage;
import to.us.mnmzc.restmote.model.message.Message;
import to.us.mnmzc.restmote.model.message.MessagePayloadType;
import to.us.mnmzc.restmote.model.message.MessageService;
import to.us.mnmzc.restmote.model.message.filter.Filter;
import to.us.mnmzc.restmote.model.transmitter.Transmitter;
import to.us.mnmzc.restmote.model.transmitter.TransmitterService;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A controller for handling the lifecycle of transmitters and sending messages.
 */
@RestController
@RequestMapping("/api/v1/transmitter")
public class TransmitterController {

    @Autowired private TransmitterService transmitterService;
    @Autowired private MessageService messageService;

    /**
     * Attempts to parse the attribute to another type, then returns
     * a string if it cannot be parsed.
     * Currently, supports parsing to:
     * - Boolean
     * - Number (Integer, Double)
     * - String (if it cannot be parsed to any other type)
     * @param attribute the attribute to parse
     * @return the parsed attribute, or the original string if it cannot be parsed
     */
    private Object tryParseAttribute(String attribute) {
        // try boolean
        if (attribute.equalsIgnoreCase("true")) { return true; }
        if (attribute.equalsIgnoreCase("false")) { return false; }

        // try number
        try {
            return Integer.parseInt(attribute);
        } catch (NumberFormatException ignored) {
            try {
                return Double.parseDouble(attribute);
            } catch (NumberFormatException ignored2) {
                // return original string
                return attribute;
            }
        }
    }

    /**
     * Extracts all the params passed in the query (ignoring anything handler-specific like
     * transmitterId or payloadType, and attempts to return a Map<String, Object> of the attributes.
     * @param allParams the query parameters passed in the request
     * @return a Map<String, Object> of the attributes, with values parsed to their appropriate types where possible
     */
    private Map<String, Object> parseMessageAttributes(MultiValueMap<String, String> allParams) {
        allParams.remove("transmitterId");
        allParams.remove("payloadType");

        return allParams.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().size() == 1
                                ? tryParseAttribute(e.getValue().getFirst())
                                : e.getValue().stream().map(this::tryParseAttribute).toList() // if only one value, return the value instead of the list
                ));
    }

    /**
     * Creates a new transmitter with the given name, id, and bridge IDs. If no ID is given, a random one will be generated.
     * The transmitter will also be registered.
     * @param id (optional) the ID of the transmitter. If not given, a random one will be generated.
     * @param name the name of the transmitter
     * @param bridgeIds the IDs of the bridges to which the transmitter will transmit messages
     * @return the created transmitter
     */
    @PostMapping("/link")
    public ResponseEntity<?> linkTransmitter(@RequestParam(required = false) String id, @RequestParam String name, @RequestParam List<String> bridgeIds) {
        Transmitter.TransmitterBuilder builder = Transmitter.builder()
                .name(name)
                .bridgeIds(bridgeIds);

        if (id != null && !id.isBlank()) { builder.id(id); }

        Transmitter candidate = builder.build();
        transmitterService.register(candidate);

        return ResponseEntity.ok().body(candidate);
    }

    @DeleteMapping("/link")
    public ResponseEntity<ApiMessage> unlinkTransmitter(@RequestParam String id) {
        transmitterService.unregister(id);
        return ResponseEntity.ok().body(new ApiMessage("Removed transmitter with id " + id));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiMessage> sendMessage(@RequestParam String transmitterId, @RequestParam MessagePayloadType payloadType, @RequestBody JsonNode payload, @RequestParam(required = false) MultiValueMap<String, String> allParams) {
        Map<String, Object> attributes = parseMessageAttributes(allParams);
        Message.MessageBuilder messageBuilder = Message.builder();

        // determine transmitter
        Transmitter transmitter = transmitterService.getTransmitterById(transmitterId).orElseThrow(() -> new IllegalStateException("Transmitter with id " + transmitterId + " not found"));
        messageBuilder.source(transmitter);

        // payload
        messageBuilder.payloadType(payloadType);
        messageBuilder.payload(payload);

        // attributes
        if (attributes != null) {
            messageBuilder.attributes(attributes);
        }

        // route message
        Message message = messageBuilder.build();
        messageService.routeMessage(message);
        return ResponseEntity.ok().body(new ApiMessage("Message sent successfully"));
    }

}
