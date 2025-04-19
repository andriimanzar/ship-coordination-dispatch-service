package com.challenge.ship.coordination.dispatchservice.repository;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class ShipRepository {

  private final Map<String, List<ShipPosition>> shipPositionsRegistry = new ConcurrentHashMap<>();

  public synchronized void addPosition(String shipId, ShipPosition shipPosition) {
    shipPositionsRegistry.computeIfAbsent(shipId, k -> new ArrayList<>())
        .add(shipPosition);
  }

  public List<ShipPosition> getShipPositions(String shipId) {
    return shipPositionsRegistry.getOrDefault(shipId, new ArrayList<>());
  }

  public Map<String, List<ShipPosition>> getAllShipsPositions() {
    return new HashMap<>(shipPositionsRegistry);
  }

  public void clear() {
    shipPositionsRegistry.clear();
  }
}
