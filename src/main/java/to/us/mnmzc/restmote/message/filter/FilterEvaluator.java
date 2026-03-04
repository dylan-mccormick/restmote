package to.us.mnmzc.restmote.message.filter;

import java.util.Map;

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
