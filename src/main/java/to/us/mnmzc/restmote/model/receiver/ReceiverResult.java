package to.us.mnmzc.restmote.model.receiver;

/**
 * Represents the result of trying to deliver a message to a receiver.
 * DELIVERED - successfully delivered to the receiver
 * DROPPED - the message was delivered but the receiver independently did not deliver it
 * FAILED - the message was not delivered due to an error (e.g. network error, receiver not responding, etc.)
 */
public enum ReceiverResult {
    /** Successfully delivered to the receiver */
    DELIVERED,
    /** The message was delivered but the receiver independently did not deliver it */
    DROPPED,
    /** The message was not delivered due to an error (e.g. network error, receiver not responding, etc.) */
    FAILED
}
