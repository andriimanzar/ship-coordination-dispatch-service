package com.challenge.ship.coordination.dispatchservice.dto;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;

public record ShipStatusResponse(String id, int lastTime, ThreatStatus lastStatus, int lastSpeed,
                                 Position lastPosition) {

  public ShipStatusResponse(String id, ShipPosition lastPosition) {
    this(id, lastPosition.time(), lastPosition.status(),
        lastPosition.speed(),
        new Position(lastPosition.x(), lastPosition.y()));
  }

}
