package to.us.mnmzc.restmote.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import to.us.mnmzc.restmote.model.transmitter.Transmitter;
import to.us.mnmzc.restmote.model.transmitter.TransmitterService;

import java.util.stream.IntStream;

@DisplayName("TransmitterServiceTests")
public class TransmitterServiceTests {

    private TransmitterService transmitterService;

    @BeforeEach
    void setUp() {
        transmitterService = new TransmitterService();
    }

    private Transmitter createTransmitter(String id, String name) {
        return Transmitter.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Nested
    @DisplayName("initialization")
    class Initialization {

        @Test
        @DisplayName("starts with an empty transmitter list")
        void startsEmpty() {
            Assertions.assertTrue(transmitterService.getTransmitters().isEmpty());
        }
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("registers a transmitter with a unique name")
        void registersUniqueName() {
            Transmitter transmitter = createTransmitter("id-1", "alpha");

            transmitterService.register(transmitter);

            Assertions.assertEquals(1, transmitterService.getTransmitters().size());
            Assertions.assertTrue(transmitterService.getTransmitters().contains(transmitter));
        }

        @Test
        @DisplayName("registers multiple transmitters when names are unique")
        void registersMultipleUniqueNames() {
            Transmitter t1 = createTransmitter("id-1", "alpha");
            Transmitter t2 = createTransmitter("id-2", "beta");
            Transmitter t3 = createTransmitter("id-3", "gamma");

            transmitterService.register(t1);
            transmitterService.register(t2);
            transmitterService.register(t3);

            Assertions.assertEquals(3, transmitterService.getTransmitters().size());
        }

        @Test
        @DisplayName("rejects registration when transmitter name already exists")
        void rejectsDuplicateName() {
            transmitterService.register(createTransmitter("id-1", "alpha"));

            Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> transmitterService.register(createTransmitter("id-2", "alpha"))
            );
        }

        @Test
        @DisplayName("allows reusing a name after previous transmitter is unregistered")
        void allowsNameReuseAfterUnregister() {
            transmitterService.register(createTransmitter("id-1", "alpha"));
            transmitterService.unregister("alpha");

            Assertions.assertDoesNotThrow(
                    () -> transmitterService.register(createTransmitter("id-2", "alpha"))
            );
            Assertions.assertEquals(1, transmitterService.getTransmitters().size());
        }

        @Test
        @DisplayName("supports many unique registrations")
        void supportsManyUniqueRegistrations() {
            IntStream.range(0, 100).forEach(i ->
                    transmitterService.register(createTransmitter("id-" + i, "name-" + i))
            );

            Assertions.assertEquals(100, transmitterService.getTransmitters().size());
        }
    }

    @Nested
    @DisplayName("unregister")
    class Unregister {

        @Test
        @DisplayName("unregisters a previously registered transmitter by name")
        void unregistersRegisteredName() {
            Transmitter t = createTransmitter("id-1", "alpha");
            transmitterService.register(t);

            transmitterService.unregister("alpha");

            Assertions.assertTrue(transmitterService.getTransmitters().isEmpty());
        }

        @Test
        @DisplayName("throws when unregistering a name that is not registered")
        void throwsWhenNameNotRegistered() {
            Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> transmitterService.unregister("missing")
            );
        }

        @Test
        @DisplayName("unregistering one transmitter does not remove other registered transmitters")
        void unregisterOneDoesNotAffectOthers() {
            Transmitter t1 = createTransmitter("id-1", "alpha");
            Transmitter t2 = createTransmitter("id-2", "beta");
            transmitterService.register(t1);
            transmitterService.register(t2);

            transmitterService.unregister("alpha");

            Assertions.assertEquals(1, transmitterService.getTransmitters().size());
            Assertions.assertTrue(transmitterService.getTransmitters().contains(t2));
        }

        @Test
        @DisplayName("throws when unregistering from empty service")
        void throwsWhenUnregisteringFromEmptyService() {
            Assertions.assertThrows(
                    IllegalStateException.class,
                    () -> transmitterService.unregister("alpha")
            );
        }
    }

    @Nested
    @DisplayName("getTransmitterById")
    class GetById {

        @Test
        @DisplayName("returns transmitter when id is registered")
        void returnsPresentWhenRegistered() {
            Transmitter t = createTransmitter("id-1", "alpha");
            transmitterService.register(t);

            var result = transmitterService.getTransmitterById("id-1");

            Assertions.assertTrue(result.isPresent());
            Assertions.assertEquals(t, result.get());
        }

        @Test
        @DisplayName("returns empty when id is not registered")
        void returnsEmptyWhenMissing() {
            transmitterService.register(createTransmitter("id-1", "alpha"));

            var result = transmitterService.getTransmitterById("id-404");

            Assertions.assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty after the transmitter has been unregistered")
        void returnsEmptyAfterUnregister() {
            transmitterService.register(createTransmitter("id-1", "alpha"));
            transmitterService.unregister("alpha");

            var result = transmitterService.getTransmitterById("id-1");

            Assertions.assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns the correct transmitter when multiple are registered")
        void returnsCorrectTransmitterAmongMany() {
            Transmitter t1 = createTransmitter("id-1", "alpha");
            Transmitter t2 = createTransmitter("id-2", "beta");
            Transmitter t3 = createTransmitter("id-3", "gamma");
            transmitterService.register(t1);
            transmitterService.register(t2);
            transmitterService.register(t3);

            var result = transmitterService.getTransmitterById("id-2");

            Assertions.assertTrue(result.isPresent());
            Assertions.assertEquals("beta", result.get().getName());
        }
    }
}
