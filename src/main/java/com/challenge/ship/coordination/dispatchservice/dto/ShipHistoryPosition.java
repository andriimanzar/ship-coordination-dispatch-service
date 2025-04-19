package com.challenge.ship.coordination.dispatchservice.dto;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;

public record ShipHistoryPosition(int time, int speed, Position position) {

  public ShipHistoryPosition(ShipPosition shipPosition) {
    this(shipPosition.time(), shipPosition.speed(),
        new Position(shipPosition.x(), shipPosition.y()));
  }
}
