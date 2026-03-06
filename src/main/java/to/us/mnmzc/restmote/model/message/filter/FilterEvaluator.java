package to.us.mnmzc.restmote.model.message.filter;

import java.util.Map;

/**
 * Evaluates a filter against a set of message attributes. It checks if all conditions of the filter are satisfied by the provided attributes.
 */
public final class FilterEvaluator {
    private FilterEvaluator() {}

    /**
     * Evaluates the given filter against the provided message attributes.
     * @param filter the filter to be evaluated
     * @param attributes the message attributes to be evaluated against the filter conditions
     * @return true if all filter conditions evaluate to true, false otherwise
     */
    public static boolean evaluate(Filter filter, Map<String, Object> attributes) {
        return filter.getConditions().stream()
                .allMatch(condition -> condition.evaluate(attributes));
    }
}
