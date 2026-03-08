package to.us.mnmzc.restmote.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import to.us.mnmzc.restmote.api.v1.receiver.WebSocketReceiverHandler;

/**
 * Configuration for WebSockets.
 */
@Configuration
@EnableWebSocket
@NullMarked
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired private WebSocketReceiverHandler webSocketReceiverHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketReceiverHandler, "/api/v1/receiver/ws").setAllowedOrigins("*");
    }

}
