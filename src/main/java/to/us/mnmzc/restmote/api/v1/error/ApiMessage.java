package to.us.mnmzc.restmote.api.v1.error;

/**
 * Basic error response for the API. Currently contains only a message, but is
 * designed to be more extensible for future use.
 * @param message the error message to be returned to the client
 */
public record ApiMessage(String message) {}
