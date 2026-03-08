package to.us.mnmzc.restmote.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import to.us.mnmzc.restmote.model.message.filter.Filter;
import to.us.mnmzc.restmote.model.message.filter.FilterCondition;
import to.us.mnmzc.restmote.model.message.filter.FilterEvaluator;
import to.us.mnmzc.restmote.model.message.filter.FilterOperator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FilterTests")
public class FilterTests {

  Filter getFilterWithConditions(List<FilterCondition> conditions) {
    return Filter.builder().conditions(conditions).build();
  }

  @Test
  @DisplayName("Tests filter evaluation with all conditions")
  void testSimpleFilterEvaluation() {
    Filter filter =
        getFilterWithConditions(
            List.of(
                new FilterCondition("key1", FilterOperator.EQ, "value1"),
                new FilterCondition("key2", FilterOperator.NEQ, "value2"),
                new FilterCondition("key3", FilterOperator.GT, 10),
                new FilterCondition("key4", FilterOperator.LT, 20),
                new FilterCondition("key5", FilterOperator.CONTAINS, "val")));

    Map<String, Object> attributes =
        Map.of(
            "key1", "value1",
            "key2", "otherValue",
            "key3", 15,
            "key4", 10,
            "key5", "some value");

    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("EQ operator - equal values")
  void testEQOperatorTrue() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("name", FilterOperator.EQ, "John")));
    Map<String, Object> attributes = Map.of("name", "John");
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("EQ operator - unequal values")
  void testEQOperatorFalse() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("name", FilterOperator.EQ, "John")));
    Map<String, Object> attributes = Map.of("name", "Jane");
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("NEQ operator - unequal values")
  void testNEQOperatorTrue() {
    Filter filter =
        getFilterWithConditions(
            List.of(new FilterCondition("status", FilterOperator.NEQ, "inactive")));
    Map<String, Object> attributes = Map.of("status", "active");
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("NEQ operator - equal values")
  void testNEQOperatorFalse() {
    Filter filter =
        getFilterWithConditions(
            List.of(new FilterCondition("status", FilterOperator.NEQ, "active")));
    Map<String, Object> attributes = Map.of("status", "active");
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("GT operator - greater than")
  void testGTOperatorTrue() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("age", FilterOperator.GT, 18)));
    Map<String, Object> attributes = Map.of("age", 25);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("GT operator - not greater than")
  void testGTOperatorFalse() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("age", FilterOperator.GT, 25)));
    Map<String, Object> attributes = Map.of("age", 18);
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("GT operator - equal values")
  void testGTOperatorEqual() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("age", FilterOperator.GT, 25)));
    Map<String, Object> attributes = Map.of("age", 25);
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("LT operator - less than")
  void testLTOperatorTrue() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("age", FilterOperator.LT, 30)));
    Map<String, Object> attributes = Map.of("age", 20);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("LT operator - not less than")
  void testLTOperatorFalse() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("age", FilterOperator.LT, 20)));
    Map<String, Object> attributes = Map.of("age", 30);
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("GTE operator - greater than or equal")
  void testGTEOperatorTrue() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("score", FilterOperator.GTE, 100)));
    Map<String, Object> attributes = Map.of("score", 100);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("GTE operator - greater value")
  void testGTEOperatorGreater() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("score", FilterOperator.GTE, 100)));
    Map<String, Object> attributes = Map.of("score", 150);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("GTE operator - less value")
  void testGTEOperatorFalse() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("score", FilterOperator.GTE, 100)));
    Map<String, Object> attributes = Map.of("score", 50);
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("LTE operator - less than or equal")
  void testLTEOperatorTrue() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("score", FilterOperator.LTE, 100)));
    Map<String, Object> attributes = Map.of("score", 100);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("LTE operator - less value")
  void testLTEOperatorLess() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("score", FilterOperator.LTE, 100)));
    Map<String, Object> attributes = Map.of("score", 75);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("LTE operator - greater value")
  void testLTEOperatorFalse() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("score", FilterOperator.LTE, 100)));
    Map<String, Object> attributes = Map.of("score", 150);
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("CONTAINS operator - substring present")
  void testCONTAINSOperatorTrue() {
    Filter filter =
        getFilterWithConditions(
            List.of(new FilterCondition("description", FilterOperator.CONTAINS, "Java")));
    Map<String, Object> attributes = Map.of("description", "This is a Java project");
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("CONTAINS operator - substring not present")
  void testCONTAINSOperatorFalse() {
    Filter filter =
        getFilterWithConditions(
            List.of(new FilterCondition("description", FilterOperator.CONTAINS, "Python")));
    Map<String, Object> attributes = Map.of("description", "This is a Java project");
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("CONTAINS operator - case sensitive")
  void testCONTAINSOperatorCaseSensitive() {
    Filter filter =
        getFilterWithConditions(
            List.of(new FilterCondition("description", FilterOperator.CONTAINS, "java")));
    Map<String, Object> attributes = Map.of("description", "This is a Java project");
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("IN operator - value present in list")
  void testINOperatorTrue() {
    Filter filter =
        getFilterWithConditions(
            List.of(
                new FilterCondition(
                    "status", FilterOperator.IN, List.of("active", "pending", "completed"))));
    Map<String, Object> attributes = Map.of("status", "active");
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("IN operator - value not in list")
  void testINOperatorFalse() {
    Filter filter =
        getFilterWithConditions(
            List.of(
                new FilterCondition("status", FilterOperator.IN, List.of("active", "pending"))));
    Map<String, Object> attributes = Map.of("status", "deleted");
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  // Edge Cases

  @Test
  @DisplayName("Edge case - null attribute value with EQ")
  void testNullAttributeValue() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("name", FilterOperator.EQ, "John")));
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("name", null);
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - null filter value with EQ")
  void testNullFilterValue() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("name", FilterOperator.EQ, null)));
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("name", null);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - missing attribute key")
  void testMissingAttributeKey() {
    Filter filter =
        getFilterWithConditions(
            List.of(new FilterCondition("missing", FilterOperator.EQ, "value")));
    Map<String, Object> attributes = Map.of("other", "value");
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - empty string value")
  void testEmptyStringValue() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("text", FilterOperator.EQ, "")));
    Map<String, Object> attributes = Map.of("text", "");
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - CONTAINS with empty string")
  void testCONTAINSEmptyString() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("text", FilterOperator.CONTAINS, "")));
    Map<String, Object> attributes = Map.of("text", "anything");
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - numeric string comparison")
  void testNumericStringComparison() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("id", FilterOperator.EQ, "123")));
    Map<String, Object> attributes = Map.of("id", "123");
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - GT with negative numbers")
  void testGTNegativeNumbers() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("temp", FilterOperator.GT, -10)));
    Map<String, Object> attributes = Map.of("temp", -5);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - LT with negative numbers")
  void testLTNegativeNumbers() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("temp", FilterOperator.LT, -5)));
    Map<String, Object> attributes = Map.of("temp", -10);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - floating point comparison")
  void testFloatingPointComparison() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("price", FilterOperator.GT, 19.99)));
    Map<String, Object> attributes = Map.of("price", 20.01);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - IN operator with empty list")
  void testINOperatorEmptyList() {
    Filter filter =
        getFilterWithConditions(
            List.of(new FilterCondition("status", FilterOperator.IN, List.of())));
    Map<String, Object> attributes = Map.of("status", "active");
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - multiple conditions all true")
  void testMultipleConditionsAllTrue() {
    Filter filter =
        getFilterWithConditions(
            List.of(
                new FilterCondition("key1", FilterOperator.EQ, "value1"),
                new FilterCondition("key2", FilterOperator.NEQ, "value2"),
                new FilterCondition("key3", FilterOperator.GT, 10)));
    Map<String, Object> attributes =
        Map.of(
            "key1", "value1",
            "key2", "otherValue",
            "key3", 15);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - multiple conditions one false")
  void testMultipleConditionsOneFalse() {
    Filter filter =
        getFilterWithConditions(
            List.of(
                new FilterCondition("key1", FilterOperator.EQ, "value1"),
                new FilterCondition("key2", FilterOperator.EQ, "value2"),
                new FilterCondition("key3", FilterOperator.GT, 10)));
    Map<String, Object> attributes =
        Map.of(
            "key1", "value1",
            "key2", "wrongValue",
            "key3", 15);
    Assertions.assertFalse(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - zero value comparison")
  void testZeroValueComparison() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("count", FilterOperator.EQ, 0)));
    Map<String, Object> attributes = Map.of("count", 0);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - boolean value comparison")
  void testBooleanValueComparison() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("active", FilterOperator.EQ, true)));
    Map<String, Object> attributes = Map.of("active", true);
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }

  @Test
  @DisplayName("Edge case - NEQ with null")
  void testNEQWithNull() {
    Filter filter =
        getFilterWithConditions(List.of(new FilterCondition("value", FilterOperator.NEQ, null)));
    Map<String, Object> attributes = Map.of("value", "something");
    Assertions.assertTrue(FilterEvaluator.evaluate(filter, attributes));
  }
}
