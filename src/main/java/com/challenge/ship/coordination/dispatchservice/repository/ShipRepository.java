package com.challenge.ship.coordination.dispatchservice.repository;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ShipRepository {

  Map<String, List<ShipPosition>> getAllShipsPositions();

  Optional<List<ShipPosition>> getShipPositions(String shipId);

  void submitPosition(String shipId, ShipPosition shipPosition);

}
