package to.us.mnmzc.restmote.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import to.us.mnmzc.restmote.model.bridge.Bridge;
import to.us.mnmzc.restmote.model.bridge.BridgeService;
import to.us.mnmzc.restmote.model.message.Message;
import to.us.mnmzc.restmote.model.message.MessagePayloadType;
import to.us.mnmzc.restmote.model.message.MessageService;
import to.us.mnmzc.restmote.model.message.filter.Filter;
import to.us.mnmzc.restmote.model.message.filter.FilterEvaluator;
import to.us.mnmzc.restmote.model.receiver.ReceiverRegistry;
import to.us.mnmzc.restmote.model.receiver.ReceiverResult;
import to.us.mnmzc.restmote.model.receiver.ReceiverSession;
import to.us.mnmzc.restmote.model.receiver.strategy.ReceiverStrategy;
import to.us.mnmzc.restmote.model.transmitter.Transmitter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("MessageServiceTests")
public class MessageServiceTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MessageService messageService;
    private BridgeService bridgeService;
    private ReceiverRegistry receiverRegistry;

    @BeforeEach
    void setUp() {
        messageService = new MessageService();
        bridgeService = mock(BridgeService.class);
        receiverRegistry = mock(ReceiverRegistry.class);

        inject(messageService, "bridgeService", bridgeService);
        inject(messageService, "receiverRegistry", receiverRegistry);
    }

    private static void inject(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field: " + fieldName, e);
        }
    }

    private Transmitter transmitter(String id, List<String> bridgeIds) {
        return Transmitter.builder().id(id).bridgeIds(bridgeIds).build();
    }

    private Message message(Transmitter source, Map<String, Object> attributes, Filter receiverFilter) {
        return Message.builder()
                .source(source)
                .attributes(attributes)
                .receiverFilter(receiverFilter)
                .payloadType(MessagePayloadType.TEXT)
                .payload(objectMapper.convertValue(Map.of("content", "hello"), JsonNode.class))
                .build();
    }

    private Bridge bridge(String id, Filter filter) {
        Bridge b = mock(Bridge.class);
        when(b.getId()).thenReturn(id);
        when(b.getFilter()).thenReturn(filter);
        return b;
    }

    private ReceiverSession receiver(String id, List<String> bridgeIds, Map<String, Object> attributes, Filter filter, ReceiverStrategy strategy) {
        return ReceiverSession.builder()
                .id(id)
                .bridgeIds(bridgeIds)
                .attributes(attributes)
                .filter(filter)
                .strategy(strategy)
                .build();
    }

    @Test
    @DisplayName("successfully routes a message (no attributes, no filters)")
    void testRouteUnfilteredMessage() {
        Transmitter tx = transmitter("abc-123", List.of("bridge-1"));
        Message msg = message(tx, Map.of(), null);

        Bridge b1 = bridge("bridge-1", null);
        ReceiverStrategy strategy = mock(ReceiverStrategy.class);
        when(strategy.deliver(msg)).thenReturn(ReceiverResult.DELIVERED);
        ReceiverSession r1 = receiver("receiver-1", List.of("bridge-1"), Map.of(), null, strategy);

        when(bridgeService.getBridgesByIds(tx.getBridgeIds())).thenReturn(List.of(b1));
        when(receiverRegistry.getSessionsForBridge("bridge-1")).thenReturn(List.of(r1));

        messageService.routeMessage(msg);

        verify(strategy, times(1)).deliver(msg);
        verify(receiverRegistry, times(1)).getSessionsForBridge("bridge-1");
    }

    @Test
    @DisplayName("does not deliver when no corresponding bridges are found")
    void routeMessage_noBridges_noDelivery() {
        Transmitter tx = transmitter("tx-1", List.of("missing-bridge"));
        Message msg = message(tx, Map.of("x", 1), null);

        when(bridgeService.getBridgesByIds(tx.getBridgeIds())).thenReturn(List.of());

        messageService.routeMessage(msg);

        verify(receiverRegistry, never()).getSessionsForBridge(anyString());
    }

    @Test
    @DisplayName("does not deliver when bridge filter rejects message attributes")
    void routeMessage_bridgeFilterRejects_noReceiverLookupOrDelivery() {
        Transmitter tx = transmitter("tx-1", List.of("bridge-1"));
        Message msg = message(tx, Map.of("room", "kitchen"), null);

        Filter bridgeFilter = mock(Filter.class);
        Bridge b1 = bridge("bridge-1", bridgeFilter);

        when(bridgeService.getBridgesByIds(tx.getBridgeIds())).thenReturn(List.of(b1));

        try (MockedStatic<FilterEvaluator> mocked = Mockito.mockStatic(FilterEvaluator.class)) {
            mocked.when(() -> FilterEvaluator.evaluate(bridgeFilter, msg.getAttributes())).thenReturn(false);

            messageService.routeMessage(msg);

            verify(receiverRegistry, never()).getSessionsForBridge(anyString());
            mocked.verify(() -> FilterEvaluator.evaluate(bridgeFilter, msg.getAttributes()), times(1));
        }
    }

    @Test
    @DisplayName("does not deliver when receiver filter rejects message attributes")
    void routeMessage_receiverFilterRejects_noDelivery() {
        Transmitter tx = transmitter("tx-1", List.of("bridge-1"));
        Message msg = message(tx, Map.of("mode", "night"), null);

        Bridge b1 = bridge("bridge-1", null);
        Filter receiverFilter = mock(Filter.class);
        ReceiverStrategy strategy = mock(ReceiverStrategy.class);
        ReceiverSession r1 = receiver("r1", List.of("bridge-1"), Map.of("name", "alpha"), receiverFilter, strategy);

        when(bridgeService.getBridgesByIds(tx.getBridgeIds())).thenReturn(List.of(b1));
        when(receiverRegistry.getSessionsForBridge("bridge-1")).thenReturn(List.of(r1));

        try (MockedStatic<FilterEvaluator> mocked = Mockito.mockStatic(FilterEvaluator.class)) {
            mocked.when(() -> FilterEvaluator.evaluate(receiverFilter, msg.getAttributes())).thenReturn(false);

            messageService.routeMessage(msg);

            verify(strategy, never()).deliver(any(Message.class));
            mocked.verify(() -> FilterEvaluator.evaluate(receiverFilter, msg.getAttributes()), times(1));
        }
    }

    @Test
    @DisplayName("message receiverFilter is applied against receiver attributes")
    void routeMessage_messageReceiverFilter_selectsMatchingReceiversOnly() {
        Transmitter tx = transmitter("tx-1", List.of("bridge-1"));
        Filter messageReceiverFilter = mock(Filter.class);
        Message msg = message(tx, Map.of("event", "click"), messageReceiverFilter);

        Bridge b1 = bridge("bridge-1", null);

        ReceiverStrategy s1 = mock(ReceiverStrategy.class);
        ReceiverStrategy s2 = mock(ReceiverStrategy.class);
        when(s1.deliver(msg)).thenReturn(ReceiverResult.DELIVERED);

        ReceiverSession r1 = receiver("r1", List.of("bridge-1"), Map.of("zone", "kitchen"), null, s1);
        ReceiverSession r2 = receiver("r2", List.of("bridge-1"), Map.of("zone", "garage"), null, s2);

        when(bridgeService.getBridgesByIds(tx.getBridgeIds())).thenReturn(List.of(b1));
        when(receiverRegistry.getSessionsForBridge("bridge-1")).thenReturn(List.of(r1, r2));

        try (MockedStatic<FilterEvaluator> mocked = Mockito.mockStatic(FilterEvaluator.class)) {
            mocked.when(() -> FilterEvaluator.evaluate(messageReceiverFilter, r1.getAttributes())).thenReturn(true);
            mocked.when(() -> FilterEvaluator.evaluate(messageReceiverFilter, r2.getAttributes())).thenReturn(false);

            messageService.routeMessage(msg);

            verify(s1, times(1)).deliver(msg);
            verify(s2, never()).deliver(any(Message.class));
        }
    }

    @Test
    @DisplayName("handles null message attributes when no filters are present")
    void routeMessage_nullAttributes_noFilters_stillDelivers() {
        Transmitter tx = transmitter("tx-1", List.of("bridge-1"));
        Message msg = message(tx, null, null);

        Bridge b1 = bridge("bridge-1", null);
        ReceiverStrategy strategy = mock(ReceiverStrategy.class);
        when(strategy.deliver(msg)).thenReturn(ReceiverResult.DELIVERED);
        ReceiverSession r1 = receiver("r1", List.of("bridge-1"), Map.of(), null, strategy);

        when(bridgeService.getBridgesByIds(tx.getBridgeIds())).thenReturn(List.of(b1));
        when(receiverRegistry.getSessionsForBridge("bridge-1")).thenReturn(List.of(r1));

        messageService.routeMessage(msg);

        verify(strategy, times(1)).deliver(msg);
    }

    @Test
    @DisplayName("routes across multiple bridges and delivers to all matching receivers")
    void routeMessage_multipleBridges_multipleDeliveries() {
        Transmitter tx = transmitter("tx-1", List.of("bridge-1", "bridge-2"));
        Message msg = message(tx, Map.of("k", "v"), null);

        Bridge b1 = bridge("bridge-1", null);
        Bridge b2 = bridge("bridge-2", null);

        ReceiverStrategy s1 = mock(ReceiverStrategy.class);
        ReceiverStrategy s2 = mock(ReceiverStrategy.class);
        when(s1.deliver(msg)).thenReturn(ReceiverResult.DELIVERED);
        when(s2.deliver(msg)).thenReturn(ReceiverResult.DROPPED);

        ReceiverSession r1 = receiver("r1", List.of("bridge-1"), Map.of(), null, s1);
        ReceiverSession r2 = receiver("r2", List.of("bridge-2"), Map.of(), null, s2);

        when(bridgeService.getBridgesByIds(tx.getBridgeIds())).thenReturn(List.of(b1, b2));
        when(receiverRegistry.getSessionsForBridge("bridge-1")).thenReturn(List.of(r1));
        when(receiverRegistry.getSessionsForBridge("bridge-2")).thenReturn(List.of(r2));

        messageService.routeMessage(msg);

        verify(s1, times(1)).deliver(msg);
        verify(s2, times(1)).deliver(msg);
    }

    @Test
    @DisplayName("does not deliver when bridge exists but has no registered receivers")
    void routeMessage_noReceiversForBridge_noDelivery() {
        Transmitter tx = transmitter("tx-1", List.of("bridge-1"));
        Message msg = message(tx, Map.of("kind", "test"), null);

        Bridge b1 = bridge("bridge-1", null);
        when(bridgeService.getBridgesByIds(tx.getBridgeIds())).thenReturn(List.of(b1));
        when(receiverRegistry.getSessionsForBridge("bridge-1")).thenReturn(List.of());

        messageService.routeMessage(msg);

        verify(receiverRegistry, times(1)).getSessionsForBridge("bridge-1");
    }
}
