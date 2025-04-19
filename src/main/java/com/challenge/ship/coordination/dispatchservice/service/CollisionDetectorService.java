package com.challenge.ship.coordination.dispatchservice.service;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;
import com.challenge.ship.coordination.dispatchservice.model.VelocityVector;
import com.challenge.ship.coordination.dispatchservice.repository.ShipRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollisionDetectorService {

  private final ShipRepository shipRepository;
  private final VelocityService velocityService;

  public ThreatStatus assesThreat(String shipId, TemporalPosition curr) {
    List<ShipPosition> currentHistory = shipRepository.getShipPositions(shipId)
        .orElse(new ArrayList<>());
    if (currentHistory.size() < 2) {
      return ThreatStatus.GREEN;
    }

    VelocityVector ownVelocityVector = velocityService.
        calculateVelocityVector(currentHistory.get(currentHistory.size() - 2), curr);

    ThreatStatus maxThreat = ThreatStatus.GREEN;
    for (Map.Entry<String, List<ShipPosition>> shipHistory : shipRepository.getAllShipsPositions()
        .entrySet()) {
      boolean isTargetShip = shipHistory.getKey().equals(shipId);

      List<ShipPosition> otherHistory = shipHistory.getValue();
      boolean hasSufficientPositionHistory = shipHistory.getValue().size() >= 2;
      if (isTargetShip || !hasSufficientPositionHistory) {
        continue;
      }

      ShipPosition otherLast = otherHistory.getLast();
      VelocityVector otherVelocityVector = velocityService.calculateVelocityVector(
          otherHistory.get(otherHistory.size() - 2),
          otherLast);

      for (int t = 1; t <= 60; t++) {
        double predictedCurrX = curr.x() + ownVelocityVector.vx() * t;
        double predictedCurrY = curr.y() + ownVelocityVector.vy() * t;

        double predictedOtherX = otherLast.x() + otherVelocityVector.vx() * t;
        double predictedOtherY = otherLast.y() + otherVelocityVector.vy() * t;

        double predictedDistance = Math.hypot(predictedCurrX - predictedOtherX,
            predictedCurrY - predictedOtherY);

        if (predictedDistance < 1.0) {
          return ThreatStatus.RED;
        } else if (predictedDistance == 1.0 && maxThreat == ThreatStatus.GREEN) {
          maxThreat = ThreatStatus.YELLOW;
        }
      }
    }
    return maxThreat;
  }
}
