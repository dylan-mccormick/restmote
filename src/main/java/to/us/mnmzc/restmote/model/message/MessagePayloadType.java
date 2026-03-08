package to.us.mnmzc.restmote.model.message;

/**
 * Contains the types of payloads that can be sent in a message. Has no effect on the message
 * itself, but can be used by the receiver to determine how to handle the payload.
 */
public enum MessagePayloadType {
  /** A stateless event. */
  BUTTON,
  /** A boolean value, true or false. */
  BOOLEAN,
  /** Any numerical value, integer or floating point. */
  NUMBER,
  /** A string. */
  TEXT,

  /** A more semantic way of representing a TEXT payload. */
  JSON,
}
