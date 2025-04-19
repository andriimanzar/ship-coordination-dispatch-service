package com.challenge.ship.coordination.dispatchservice.repository;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class ShipRepository {

  private final Map<String, List<ShipPosition>> shipPositionsRegistry = new ConcurrentHashMap<>();

  public synchronized void submitPosition(String shipId, ShipPosition shipPosition) {
    shipPositionsRegistry.computeIfAbsent(shipId, k -> new ArrayList<>())
        .add(shipPosition);
  }

  public Optional<List<ShipPosition>> getShipPositions(String shipId) {
    return Optional.ofNullable(shipPositionsRegistry.get(shipId));
  }

  public Map<String, List<ShipPosition>> getAllShipsPositions() {
    return new HashMap<>(shipPositionsRegistry);
  }

  public void clear() {
    shipPositionsRegistry.clear();
  }
}
