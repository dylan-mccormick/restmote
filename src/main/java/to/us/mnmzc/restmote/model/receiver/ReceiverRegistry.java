package to.us.mnmzc.restmote.model.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for receivers. Used to track registered receivers, and their sessions.
 */
@Component
@Slf4j
public class ReceiverRegistry {
    private final List<ReceiverSession> sessions = new CopyOnWriteArrayList<>();

    /**
     * Registers a receiver session. This should be called when a receiver connects to the server.
     * @param session The session to register.
     * @throws IllegalStateException if a session with the same ID is already registered.
     */
    public void register(ReceiverSession session) {
        // ensure we don't have duplicate sessions
        if (sessions.stream().anyMatch(s -> s.getId().equals(session.getId()))) {
            log.warn("Attempted to register session with ID {}, but it is already registered", session.getId());
            throw new IllegalStateException("Session with ID " + session.getId() + " is already registered");
        }

        sessions.add(session);
        log.info("Registered receiver session with ID {}", session.getId());
    }

    /**
     * Unregisters a receiver session. This should be called when a receiver disconnects from the server.
     * @param sessionId The ID of the session to unregister.
     * @throws IllegalStateException if a session with the given ID is not registered.
     */
    public void unregister(String sessionId) {
        if (sessions.removeIf(s -> s.getId().equals(sessionId))) {
            log.info("Unregistered receiver session with ID {}", sessionId);
            return;
        }

        log.warn("Attempted to unregister session with ID {}, but it was not found", sessionId);
        throw new IllegalStateException("Session with ID " + sessionId + " is not registered");
    }

    /**
     * Gets the sessions for a given bridge. This should be used to determine which receivers should receive a message for a given bridge.
     * @param bridgeId The ID of the bridge to get sessions for.
     * @return The list of sessions for the given bridge.
     */
    public List<ReceiverSession> getSessionsForBridge(String bridgeId) {
        return sessions.stream().filter(s -> s.getBridges().stream().anyMatch(b -> b.getId().equals(bridgeId))).toList();
    }
}
