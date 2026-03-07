package to.us.mnmzc.restmote.api.v1.transmitter;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

/**
 * A controller for handling the lifecycle of transmitters and sending messages.
 */
@RestController
@RequestMapping("/api/v1/transmitter")
public class TransmitterController {

    @Autowired private TransmitterService transmitterService;
    @Autowired private MessageService messageService;

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
    public ResponseEntity<ApiMessage> sendMessage(@RequestParam String transmitterId, @RequestParam MessagePayloadType payloadType, @RequestBody JsonNode payload, @RequestParam(required = false) Map<String, Object> attributes) {
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
