package to.us.mnmzc.restmote.model.transmitter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The transmitter service keeps track of all the connected transmitters and is responsible for validating transmitters when
 * a connection is requested.
 */
@Slf4j
@Service
public class TransmitterService {

    @Getter private final List<Transmitter> transmitters;

    /**
     * Validates the given API key and returns whether it is valid or not.
     * @param apiKey The API key to validate.
     * @return true if the API key is valid, false otherwise.
     */
    private boolean validateApiKey(String apiKey) {
        throw new UnsupportedOperationException("Implement");
    }

    /**
     * Initializes the transmitter service with an empty list of transmitters. The list of transmitters will be populated as transmitters are registered.
     */
    public TransmitterService () {
        this.transmitters = new ArrayList<>();
    }

    /**
     * Registers the given transmitter. The transmitter must have a unique id.
     * @param transmitter The transmitter to register. Must have a unique id.
     * @throws IllegalStateException if a transmitter with the same id is already registered.
     */
    public void register(Transmitter transmitter) {
        if (transmitters.stream().anyMatch(t -> t.getId().equalsIgnoreCase(transmitter.getId()))) {
            log.warn("Attempted to register transmitter with id {}, but it already exists", transmitter.getId());
            throw new IllegalStateException("Transmitter with id " + transmitter.getId() + " already exists");
        }

        transmitters.add(transmitter);
        log.info("Registered transmitter with id {}", transmitter.getId());
    }

    /**
     * Unregisters the transmitter with the given id. The transmitter must be registered for it to be unregistered.
     * @param id The id of the transmitter to unregister. The transmitter must be registered for it to be unregistered.
      * @throws IllegalStateException if a transmitter with the given id is not registered.
     */
    public void unregister(String id) {
        if (transmitters.removeIf(t -> t.getId().equalsIgnoreCase(id))) {
            log.info("Unregistered transmitter with id {}", id);
            return;
        }

        log.warn("Attempted to unregister transmitter with id {}, but it was not found", id);
        throw new IllegalStateException("Transmitter with id " + id + " is not registered");
    }

    /**
     * Gets the transmitter with the given ID. The transmitter must be registered for it to be returned.
     * @param id The ID of the transmitter to get.
     * @return An Optional containing the transmitter with the given ID if it is registered, or an empty Optional if it is not registered.
     */
    public Optional<Transmitter> getTransmitterById(String id) {
        return transmitters.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

}
