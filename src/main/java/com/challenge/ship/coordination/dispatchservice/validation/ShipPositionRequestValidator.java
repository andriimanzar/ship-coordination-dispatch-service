package com.challenge.ship.coordination.dispatchservice.validation;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class ShipPositionRequestValidator {

  public void validateCoordinates(int x, int y) {
    if (x < -50 || x > 50 || y < -50 || y > 50) {
      throw new IllegalArgumentException("coordinates out of bounds: must be in [-50; 50]");
    }
  }

  public void validateTimeIsNotGreaterThanCurrent(int timeToValidate) {
    long currTime = Instant.now().getEpochSecond();
    if (currTime < timeToValidate) {
      throw new IllegalArgumentException("time should not be greater that the current one");
    }
  }

  public void validateTimeIsGreaterThanThePreviousReceived(int timeToValidate,
      int previousReceivedTime) {
    if (timeToValidate <= previousReceivedTime) {
      throw new IllegalArgumentException(
          "time should always be greater than the previous received");
    }
  }
}
