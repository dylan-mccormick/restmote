package to.us.mnmzc.restmote.model.message;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import to.us.mnmzc.restmote.model.bridge.Bridge;
import to.us.mnmzc.restmote.model.bridge.BridgeService;
import to.us.mnmzc.restmote.model.message.filter.FilterEvaluator;
import to.us.mnmzc.restmote.model.receiver.ReceiverRegistry;
import to.us.mnmzc.restmote.model.receiver.ReceiverResult;
import to.us.mnmzc.restmote.model.receiver.ReceiverSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service is used to manage messages, e.g. by providing methods to create and send messages.
 */
@Service
@Slf4j
public class MessageService {

  @Autowired BridgeService bridgeService;
  @Autowired ReceiverRegistry receiverRegistry;

  public void routeMessage(Message message) {
    log.debug(
        "Routing message with transmitter {} and attributes {}",
        message.getSource(),
        message.getAttributes());
    List<Bridge> correspondingBridges =
        bridgeService.getBridgesByIds(message.getSource().getBridgeIds());
    log.trace("Found {} corresponding bridges.", correspondingBridges.size());

    // run filters:
    // message attributes -> bridge filters
    List<Bridge> filteredBridges =
        correspondingBridges.stream()
            .filter(
                bridge ->
                    bridge.getFilter() == null
                        || FilterEvaluator.evaluate(bridge.getFilter(), message.getAttributes()))
            .toList();
    log.trace("After filtering, {} corresponding bridges remain.", filteredBridges.size());

    // message attributes -> receiver filters
    List<ReceiverSession> initiallyFilteredReceivers =
        filteredBridges.stream()
            .flatMap(
                b ->
                    receiverRegistry.getSessionsForBridge(b.getId()).stream()
                        .filter(
                            r ->
                                r.getFilter() == null
                                    || FilterEvaluator.evaluate(
                                        r.getFilter(), message.getAttributes())))
            .toList();
    log.trace(
        "After filtering, {} corresponding receivers remain.", initiallyFilteredReceivers.size());

    // receiver attributes -> message filters
    List<ReceiverSession> toReceive =
        initiallyFilteredReceivers.stream()
            .filter(
                r ->
                    message.getReceiverFilter() == null
                        || FilterEvaluator.evaluate(message.getReceiverFilter(), r.getAttributes()))
            .toList();
    log.trace("Message to be sent to {} receivers.", toReceive.size());

    // deliver message and aggregate results
    List<ReceiverResult> results =
        toReceive.stream().map(r -> r.getStrategy().deliver(message)).toList();
    log.debug(
        "Message delivered to {} receivers. DELIVERED: {}, DROPPED: {}, FAILED: {}",
        results.size(),
        results.stream().filter(r -> r == ReceiverResult.DELIVERED).count(),
        results.stream().filter(r -> r == ReceiverResult.DROPPED).count(),
        results.stream().filter(r -> r == ReceiverResult.FAILED).count());
  }
}
