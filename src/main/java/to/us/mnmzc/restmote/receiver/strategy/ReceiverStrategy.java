package to.us.mnmzc.restmote.receiver.strategy;

import to.us.mnmzc.restmote.message.Message;
import to.us.mnmzc.restmote.receiver.ReceiverResult;

/**
 * Interface for receiver strategies. A receiver strategy is responsible for delivering a message to a receiver.
 */
public interface ReceiverStrategy {
    /**
     * Delivers a message to a receiver. The implementation of this method is responsible for determining how to deliver the message.
     * @param message the message to deliver
     * @return a ReceiverResult indicating the result of the delivery attempt
     */
    ReceiverResult deliver(Message message);
}
