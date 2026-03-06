package to.us.mnmzc.restmote.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import to.us.mnmzc.restmote.model.bridge.Bridge;
import to.us.mnmzc.restmote.model.receiver.ReceiverRegistry;
import to.us.mnmzc.restmote.model.receiver.ReceiverSession;

import java.util.List;

@DisplayName("ReceiverRegistryTests")
public class ReceiverRegistryTests {

    ReceiverSession getSession() {
        return ReceiverSession.builder()
                .bridgeIds(List.of("test-bridge"))
                .build();
    }

    ReceiverSession getSessionWithBridge(String bridgeId) {
        return ReceiverSession.builder()
                .bridgeIds(List.of(bridgeId))
                .build();
    }

    ReceiverSession getSessionWithMultipleBridges(String... bridgeIds) {
        return ReceiverSession.builder()
                .bridgeIds(List.of(bridgeIds))
                .build();
    }

    @Nested
    @DisplayName("register")
    class RegisterTests {

        @Test
        @DisplayName("registers a session successfully")
        void registerWorks() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSession();
            registry.register(session);

            Assertions.assertTrue(registry.getSessionsForBridge("test-bridge").contains(session));
        }

        @Test
        @DisplayName("registers session with multiple bridges")
        void registerMultipleBridges() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSessionWithMultipleBridges("bridge-1", "bridge-2", "bridge-3");
            registry.register(session);

            Assertions.assertTrue(registry.getSessionsForBridge("bridge-1").contains(session));
            Assertions.assertTrue(registry.getSessionsForBridge("bridge-2").contains(session));
            Assertions.assertTrue(registry.getSessionsForBridge("bridge-3").contains(session));
        }

        @Test
        @DisplayName("registers multiple sessions for same bridge")
        void registerMultipleSessions() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session1 = getSessionWithBridge("bridge-1");
            ReceiverSession session2 = getSessionWithBridge("bridge-1");
            ReceiverSession session3 = getSessionWithBridge("bridge-1");

            registry.register(session1);
            registry.register(session2);
            registry.register(session3);

            List<ReceiverSession> sessions = registry.getSessionsForBridge("bridge-1");
            Assertions.assertEquals(3, sessions.size());
            Assertions.assertTrue(sessions.contains(session1));
            Assertions.assertTrue(sessions.contains(session2));
            Assertions.assertTrue(sessions.contains(session3));
        }

        @Test
        @DisplayName("registers same session multiple times ERRORS")
        void registerSameSessionTwice() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSession();
            registry.register(session);
            Assertions.assertThrows(IllegalStateException.class, () -> registry.register(session));
        }

        @Test
        @DisplayName("registers session with null bridges list")
        void registerNullBridgesList() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = ReceiverSession.builder().bridgeIds(List.of()).build();

            Assertions.assertDoesNotThrow(() -> registry.register(session));
        }

        @Test
        @DisplayName("registers session with empty bridges list")
        void registerEmptyBridgesList() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = ReceiverSession.builder().bridgeIds(List.of()).build();

            Assertions.assertDoesNotThrow(() -> registry.register(session));
            Assertions.assertTrue(registry.getSessionsForBridge("any-bridge").isEmpty());
        }
    }

    @Nested
    @DisplayName("getSessionsForBridge")
    class GetSessionsTests {

        @Test
        @DisplayName("returns empty list for non-existent bridge")
        void getSessionsNonExistentBridge() {
            ReceiverRegistry registry = new ReceiverRegistry();
            List<ReceiverSession> sessions = registry.getSessionsForBridge("non-existent");

            Assertions.assertTrue(sessions.isEmpty());
        }

        @Test
        @DisplayName("returns all sessions for a bridge")
        void getSessionsForBridge() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session1 = getSessionWithBridge("bridge-1");
            ReceiverSession session2 = getSessionWithBridge("bridge-1");

            registry.register(session1);
            registry.register(session2);

            List<ReceiverSession> sessions = registry.getSessionsForBridge("bridge-1");
            Assertions.assertEquals(2, sessions.size());
            Assertions.assertTrue(sessions.contains(session1));
            Assertions.assertTrue(sessions.contains(session2));
        }

        @Test
        @DisplayName("returns sessions only for specific bridge")
        void getSessionsSpecificBridge() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session1 = getSessionWithBridge("bridge-1");
            ReceiverSession session2 = getSessionWithBridge("bridge-2");
            ReceiverSession session3 = getSessionWithMultipleBridges("bridge-1", "bridge-2");

            registry.register(session1);
            registry.register(session2);
            registry.register(session3);

            List<ReceiverSession> bridge1Sessions = registry.getSessionsForBridge("bridge-1");
            Assertions.assertEquals(2, bridge1Sessions.size());
            Assertions.assertTrue(bridge1Sessions.contains(session1));
            Assertions.assertTrue(bridge1Sessions.contains(session3));

            List<ReceiverSession> bridge2Sessions = registry.getSessionsForBridge("bridge-2");
            Assertions.assertEquals(2, bridge2Sessions.size());
            Assertions.assertTrue(bridge2Sessions.contains(session2));
            Assertions.assertTrue(bridge2Sessions.contains(session3));
        }

        @Test
        @DisplayName("returns list is mutable without affecting registry")
        void getSessionsMutability() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session1 = getSessionWithBridge("bridge-1");
            registry.register(session1);

            List<ReceiverSession> sessions = registry.getSessionsForBridge("bridge-1");
            Assertions.assertThrows(UnsupportedOperationException.class, sessions::clear);
        }

        @Test
        @DisplayName("get sessions with empty bridge id")
        void getSessionsEmptyBridgeId() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSessionWithBridge("");
            registry.register(session);

            List<ReceiverSession> sessions = registry.getSessionsForBridge("");
            Assertions.assertTrue(sessions.contains(session));
        }
    }

    @Nested
    @DisplayName("unregister")
    class UnregisterTests {

        @Test
        @DisplayName("unregisters a session successfully")
        void unregisterWorks() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSession();
            registry.register(session);
            registry.unregister(session.getId());

            Assertions.assertFalse(registry.getSessionsForBridge("test-bridge").contains(session));
        }

        @Test
        @DisplayName("unregisters session from all bridges")
        void unregisterFromAllBridges() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSessionWithMultipleBridges("bridge-1", "bridge-2", "bridge-3");
            registry.register(session);
            registry.unregister(session.getId());

            Assertions.assertTrue(registry.getSessionsForBridge("bridge-1").isEmpty());
            Assertions.assertTrue(registry.getSessionsForBridge("bridge-2").isEmpty());
            Assertions.assertTrue(registry.getSessionsForBridge("bridge-3").isEmpty());
        }

        @Test
        @DisplayName("unregister non-existent session ERRORS")
        void unregisterNonExistent() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSession();

            Assertions.assertThrows(IllegalStateException.class, () -> registry.unregister(session.getId()));
        }

        @Test
        @DisplayName("unregisters one of multiple sessions for bridge")
        void unregisterOneOfMany() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session1 = getSessionWithBridge("bridge-1");
            ReceiverSession session2 = getSessionWithBridge("bridge-1");
            ReceiverSession session3 = getSessionWithBridge("bridge-1");

            registry.register(session1);
            registry.register(session2);
            registry.register(session3);

            registry.unregister(session2.getId());

            List<ReceiverSession> sessions = registry.getSessionsForBridge("bridge-1");
            Assertions.assertEquals(2, sessions.size());
            Assertions.assertTrue(sessions.contains(session1));
            Assertions.assertFalse(sessions.contains(session2));
            Assertions.assertTrue(sessions.contains(session3));
        }

        @Test
        @DisplayName("unregister same session twice ERRORS")
        void unregisterTwice() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSession();
            registry.register(session);
            registry.unregister(session.getId());
            Assertions.assertThrows(IllegalStateException.class, () -> registry.unregister(session.getId()));
        }

        @Test
        @DisplayName("unregister from one bridge doesn't affect other bridges")
        void unregisterFromOneBridge() {
            ReceiverRegistry registry = new ReceiverRegistry();
            ReceiverSession session = getSessionWithMultipleBridges("bridge-1", "bridge-2");
            registry.register(session);

            registry.unregister(session.getId());

            Assertions.assertTrue(registry.getSessionsForBridge("bridge-1").isEmpty());
            Assertions.assertTrue(registry.getSessionsForBridge("bridge-2").isEmpty());
        }
    }
}

