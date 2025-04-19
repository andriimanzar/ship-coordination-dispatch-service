package com.challenge.ship.coordination.dispatchservice.model;

public record ShipPosition(int time, int x, int y, int speed, ThreatStatus status)
    implements TemporalPosition {

  public ShipPosition(TemporalPosition temporalPosition, int speed, ThreatStatus status) {
    this(temporalPosition.x(), temporalPosition.y(), temporalPosition.time(),
        speed, status);
  }
}
