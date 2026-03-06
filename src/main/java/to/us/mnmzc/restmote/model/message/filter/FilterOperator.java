package to.us.mnmzc.restmote.model.message.filter;

import java.util.List;
import java.util.Objects;

/**
 * Enum representing the different filter operators that can be used in a filter rule.
 */
public enum FilterOperator {
    EQ(Objects::equals),
    NEQ((a, b) -> !Objects.equals(a, b)),
    CONTAINS((a, b) -> a instanceof String && b instanceof String && a.toString().contains(b.toString())),
    GT((a, b) -> toDouble(a) > toDouble(b)),
    LT((a, b) -> toDouble(a) < toDouble(b)),
    GTE((a, b) -> toDouble(a) >= toDouble(b)),
    LTE((a, b) -> toDouble(a) <= toDouble(b)),
    IN((a, b) -> (b instanceof List<?> lb) && lb.contains(a));

    /* Methods for evaluation of the filter */
    private final FilterPredicate predicate;

    FilterOperator(FilterPredicate predicate) {
        this.predicate = predicate;
    }

    /**
     * Evaluates the filter operator against the given actual and expected values.
     * @param actual the actual value
     * @param expected the expected value
     * @return true if the filter operator evaluates to true, false otherwise
     */
    public boolean test(Object actual, Object expected) {
        return predicate.evaluate(actual, expected);
    }

    private static double toDouble(Object val) {
        return Double.parseDouble(val.toString());
    }
}
