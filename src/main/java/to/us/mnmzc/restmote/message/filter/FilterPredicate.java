package to.us.mnmzc.restmote.message.filter;

import com.google.errorprone.annotations.Immutable;

/**
 * Functional interface representing a predicate for evaluating filter rules.
 * This is used to determine whether a message matches a filter rule based on its attributes.
 */
@FunctionalInterface
@Immutable
public interface FilterPredicate {
    boolean evaluate(Object actual, Object expected);
}
