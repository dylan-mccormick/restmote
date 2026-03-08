package to.us.mnmzc.restmote.model.receiver.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import to.us.mnmzc.restmote.model.message.Message;
import to.us.mnmzc.restmote.model.receiver.ReceiverResult;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * A strategy for delivering messages to a receiver via a WebSocket connection.
 */
@Slf4j
public class WebSocketStrategy implements ReceiverStrategy {

    private final WebSocketSession webSocketSession;
    private final ObjectMapper objectMapper;

    public WebSocketStrategy(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ReceiverResult deliver(Message message) {
        try {
            String jsonMessage = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
            webSocketSession.sendMessage(new TextMessage(jsonMessage));
        } catch (IOException e) {
            log.error("Failed to send message via WebSocket", e);
            return ReceiverResult.FAILED;
        }

        return ReceiverResult.DELIVERED;
    }

}
