package com.challenge.ship.coordination.dispatchservice.model;

public record ShipPosition(long time, int x, int y, int speed, ThreatStatus status)
    implements TemporalPosition {

  public ShipPosition(TemporalPosition temporalPosition, int speed, ThreatStatus status) {
    this(temporalPosition.time(), temporalPosition.x(), temporalPosition.y(),
        speed, status);
  }
}
