package com.challenge.ship.coordination.dispatchservice.dto;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ShipStatusResponse(String id,
                                 @JsonProperty("last_time") long lastTime,
                                 @JsonProperty("last_status") ThreatStatus lastStatus,
                                 @JsonProperty("last_speed") int lastSpeed,
                                 @JsonProperty("last_position") Position lastPosition) {

  public ShipStatusResponse(String id, ShipPosition lastPosition) {
    this(id, lastPosition.time(), lastPosition.status(),
        lastPosition.speed(),
        new Position(lastPosition.x(), lastPosition.y()));
  }
}
