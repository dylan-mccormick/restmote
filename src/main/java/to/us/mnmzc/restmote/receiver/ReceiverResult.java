package to.us.mnmzc.restmote.receiver;

/**
 * Represents the result of trying to deliver a message to a receiver.
 * DELIVERED - successfully delivered to the receiver
 * DROPPED - the message was dropped by the receiver since it doesn't match the filter
 * FAILED - the message was not delivered due to an error (e.g. network error, receiver not responding, etc.)
 */
public enum ReceiverResult {
    /** Successfully delivered to the receiver */
    DELIVERED,
    /** The message was dropped by the receiver since it doesn't match the filter */
    DROPPED,
    /** The message was not delivered due to an error (e.g. network error, receiver not responding, etc.) */
    FAILED
}
