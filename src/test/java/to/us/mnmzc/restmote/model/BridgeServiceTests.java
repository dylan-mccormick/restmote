package to.us.mnmzc.restmote.model;

import java.util.List;

import to.us.mnmzc.restmote.config.BridgeConfig;
import to.us.mnmzc.restmote.model.bridge.BridgeService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@DisplayName("BridgeServiceTests")
public class BridgeServiceTests {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
          .withUserConfiguration(BridgeConfig.class, BridgeService.class);

  private ApplicationContextRunner withBaseBridges() {
    return contextRunner.withPropertyValues(
        "restmote.bridge-entries[0].id=home",
        "restmote.bridge-entries[0].name=Home Bridge",
        "restmote.bridge-entries[0].auth-token=home-secret",
        "restmote.bridge-entries[1].id=garage",
        "restmote.bridge-entries[1].name=Garage Bridge",
        "restmote.bridge-entries[1].auth-token=garage-secret",
        "restmote.bridge-entries[2].id=office",
        "restmote.bridge-entries[2].name=Office Bridge",
        "restmote.bridge-entries[2].auth-token=office-secret");
  }

  @Nested
  @DisplayName("Initialization")
  class Initialization {
    @Test
    @DisplayName("initializes bridge list without filter")
    void initializesWithoutFilter() {
      contextRunner
          .withPropertyValues(
              "restmote.bridge-entries[0].id=home",
              "restmote.bridge-entries[0].name=Home Bridge",
              "restmote.bridge-entries[0].auth-token=secret")
          .run(
              context -> {
                BridgeService service = context.getBean(BridgeService.class);

                Assertions.assertEquals(1, service.getBridges().size());
                Assertions.assertEquals("home", service.getBridges().getFirst().getId());
                Assertions.assertEquals("Home Bridge", service.getBridges().getFirst().getName());
                Assertions.assertNull(service.getBridges().getFirst().getFilter());
              });
    }

    @Test
    @DisplayName("initializes all configured bridges with mixed filter configurations")
    void initializesAllConfiguredBridgesWithMixedFilters() {
      contextRunner
          .withPropertyValues(
              "restmote.bridge-entries[0].id=home",
              "restmote.bridge-entries[0].name=Home Bridge",
              "restmote.bridge-entries[0].auth-token=home-secret",
              "restmote.bridge-entries[1].id=garage",
              "restmote.bridge-entries[1].name=Garage Bridge",
              "restmote.bridge-entries[1].auth-token=garage-secret",
              "restmote.bridge-entries[1].filter.name=Garage Filter",
              "restmote.bridge-entries[1].filter.conditions[0].field=topic",
              "restmote.bridge-entries[1].filter.conditions[0].operator=EQ",
              "restmote.bridge-entries[1].filter.conditions[0].value=garage/open",
              "restmote.bridge-entries[2].id=office",
              "restmote.bridge-entries[2].name=Office Bridge",
              "restmote.bridge-entries[2].auth-token=office-secret",
              "restmote.bridge-entries[2].filter.name=Office Filter",
              "restmote.bridge-entries[2].filter.conditions[0].field=topic",
              "restmote.bridge-entries[2].filter.conditions[0].operator=CONTAINS",
              "restmote.bridge-entries[2].filter.conditions[0].value=office/",
              "restmote.bridge-entries[2].filter.conditions[1].field=payload",
              "restmote.bridge-entries[2].filter.conditions[1].operator=NEQ",
              "restmote.bridge-entries[2].filter.conditions[1].value=ignore")
          .run(
              context -> {
                BridgeService service = context.getBean(BridgeService.class);
                Assertions.assertEquals(3, service.getBridges().size());

                var home =
                    service.getBridges().stream()
                        .filter(b -> "home".equals(b.getId()))
                        .findFirst()
                        .orElseThrow();
                var garage =
                    service.getBridges().stream()
                        .filter(b -> "garage".equals(b.getId()))
                        .findFirst()
                        .orElseThrow();
                var office =
                    service.getBridges().stream()
                        .filter(b -> "office".equals(b.getId()))
                        .findFirst()
                        .orElseThrow();

                Assertions.assertNull(home.getFilter());

                Assertions.assertNotNull(garage.getFilter());
                Assertions.assertEquals("Garage Filter", garage.getFilter().getName());
                Assertions.assertEquals(1, garage.getFilter().getConditions().size());
                Assertions.assertEquals(
                    "topic", garage.getFilter().getConditions().getFirst().field());
                Assertions.assertEquals(
                    "garage/open", garage.getFilter().getConditions().getFirst().expected());

                Assertions.assertNotNull(office.getFilter());
                Assertions.assertEquals("Office Filter", office.getFilter().getName());
                Assertions.assertEquals(2, office.getFilter().getConditions().size());
                Assertions.assertEquals("topic", office.getFilter().getConditions().get(0).field());
                Assertions.assertEquals(
                    "office/", office.getFilter().getConditions().get(0).expected());
                Assertions.assertEquals(
                    "payload", office.getFilter().getConditions().get(1).field());
                Assertions.assertEquals(
                    "ignore", office.getFilter().getConditions().get(1).expected());
              });
    }
  }

  @Nested
  @DisplayName("getBridgesByIds")
  class GetBridgesByIds {

    @Test
    @DisplayName("returns matching bridges for provided ids")
    void returnsMatchingBridgesForProvidedIds() {
      withBaseBridges()
          .run(
              context -> {
                BridgeService service = context.getBean(BridgeService.class);

                var result = service.getBridgesByIds(List.of("garage", "home"));

                Assertions.assertEquals(2, result.size());
                Assertions.assertTrue(result.stream().anyMatch(b -> "home".equals(b.getId())));
                Assertions.assertTrue(result.stream().anyMatch(b -> "garage".equals(b.getId())));
              });
    }

    @Test
    @DisplayName("returns empty list when ids are empty")
    void returnsEmptyWhenIdsAreEmpty() {
      withBaseBridges()
          .run(
              context -> {
                BridgeService service = context.getBean(BridgeService.class);

                var result = service.getBridgesByIds(List.of());

                Assertions.assertTrue(result.isEmpty());
              });
    }

    @Test
    @DisplayName("returns empty list when no ids match")
    void returnsEmptyWhenNoIdsMatch() {
      withBaseBridges()
          .run(
              context -> {
                BridgeService service = context.getBean(BridgeService.class);

                var result = service.getBridgesByIds(List.of("missing-1", "missing-2"));

                Assertions.assertTrue(result.isEmpty());
              });
    }

    @Test
    @DisplayName("returns only known bridges when ids contain unknown values")
    void returnsOnlyKnownBridgesWhenIdsContainUnknownValues() {
      withBaseBridges()
          .run(
              context -> {
                BridgeService service = context.getBean(BridgeService.class);

                var result = service.getBridgesByIds(List.of("office", "unknown"));

                Assertions.assertEquals(1, result.size());
                Assertions.assertEquals("office", result.getFirst().getId());
              });
    }

    @Test
    @DisplayName("does not duplicate bridges when input ids contain duplicates")
    void doesNotDuplicateWhenInputContainsDuplicateIds() {
      withBaseBridges()
          .run(
              context -> {
                BridgeService service = context.getBean(BridgeService.class);

                var result = service.getBridgesByIds(List.of("home", "home", "home"));

                Assertions.assertEquals(1, result.size());
                Assertions.assertEquals("home", result.getFirst().getId());
              });
    }

    @Test
    @DisplayName("returns matches in configured bridge order")
    void returnsMatchesInConfiguredOrder() {
      withBaseBridges()
          .run(
              context -> {
                BridgeService service = context.getBean(BridgeService.class);

                var result = service.getBridgesByIds(List.of("office", "home"));

                Assertions.assertEquals(
                    List.of("home", "office"), result.stream().map(b -> b.getId()).toList());
              });
    }
  }
}
