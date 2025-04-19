package com.challenge.ship.coordination.dispatchservice.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ShipPositionRequestValidator unit tests")
class ShipPositionRequestValidatorTest {

  ShipPositionRequestValidator validator;

  @BeforeEach
  void setUp() {
    validator = new ShipPositionRequestValidator();
  }

  @ParameterizedTest
  @DisplayName("validateCoordinates")
  @CsvSource({
      "0, 0, false",
      "51, 51, true",
      "-51, -51, true",
      "50, 50, false",
      "50, 51, true",
      "-50, -50, false"
  })
  @Order(1)
  void validateCoordinates(
      int x, int y, boolean shouldThrowException) {
    if (shouldThrowException) {
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class, () -> validator.validateCoordinates(x, y));
      assertEquals("coordinates out of bounds: must be in [-50; 50]",
          exception.getMessage());
    } else {
      assertDoesNotThrow(() -> validator.validateCoordinates(x, y));
    }
  }

  @ParameterizedTest
  @DisplayName("validateTimeIsNotGreaterThanCurrent")
  @MethodSource("validateTimeIsNotGreaterThanCurrentTestData")
  @Order(2)
  void validateTimeIsNotGreaterThanCurrent(long inputTime, boolean shouldThrow) {
    if (shouldThrow) {
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class,
          () -> validator.validateTimeIsNotGreaterThanCurrent(inputTime)
      );
      assertEquals("time should not be greater that the current one", exception.getMessage());
    } else {
      assertDoesNotThrow(() -> validator.validateTimeIsNotGreaterThanCurrent(inputTime));
    }
  }

  @ParameterizedTest
  @DisplayName("validateTimeIsGreaterThanThePreviousReceived")
  @CsvSource({
      "1000, 500, false",
      "500, 500, true",
      "499, 500, true"
  })
  @Order(3)
  void validateTimeIsGreaterThanThePreviousReceived(long timeToValidate, long previousReceivedTime,
      boolean shouldThrowException) {
    if (shouldThrowException) {
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class,
          () -> validator.validateTimeIsGreaterThanThePreviousReceived(timeToValidate,
              previousReceivedTime)
      );
      assertEquals("time should always be greater than the previous received",
          exception.getMessage());
    } else {
      assertDoesNotThrow(
          () -> validator.validateTimeIsGreaterThanThePreviousReceived(timeToValidate,
              previousReceivedTime));
    }
  }

  static Stream<Arguments> validateTimeIsNotGreaterThanCurrentTestData() {
    long currentTime = Instant.now().getEpochSecond();
    return Stream.of(
        Arguments.of(currentTime - 1000, false),
        Arguments.of(currentTime, false),
        Arguments.of(currentTime + 100, true));
  }
}