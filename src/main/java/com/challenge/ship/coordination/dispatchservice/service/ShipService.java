package com.challenge.ship.coordination.dispatchservice.service;

import com.challenge.ship.coordination.dispatchservice.dto.AllShipsStatusResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipHistoryResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipPositionRequest;
import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;

public interface ShipService {

  AllShipsStatusResponse getCurrentFleetStatus();

  ShipHistoryResponse getShipPositionHistory(String shipId);

  ShipPosition submitPosition(String shipId,
      ShipPositionRequest positionRequest);

  void flushData();

}
