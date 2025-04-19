package com.challenge.ship.coordination.dispatchservice.service;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;
import com.challenge.ship.coordination.dispatchservice.model.VelocityVector;
import com.challenge.ship.coordination.dispatchservice.repository.ShipRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollisionDetectorService {

  private final ShipRepository shipRepository;
  private final VelocityService velocityService;

  public ThreatStatus assesThreat(String shipId, TemporalPosition currPos) {
    List<ShipPosition> currentHistory = shipRepository.getShipPositions(shipId)
        .orElse(new ArrayList<>());

    VelocityVector ownVelocityVector = !currentHistory.isEmpty()
        ? velocityService.calculateVelocityVector(currentHistory.getLast(), currPos)
        : new VelocityVector(0, 0);

    ThreatStatus maxThreat = ThreatStatus.GREEN;

    for (Map.Entry<String, List<ShipPosition>> entry : shipRepository.getAllShipsPositions()
        .entrySet()) {
      String otherShipId = entry.getKey();
      List<ShipPosition> otherHistory = entry.getValue();
      if (otherShipId.equals(shipId) || otherHistory.isEmpty()) {
        continue;
      }

      Optional<ShipPosition> otherPositionBeforeCurrOpt = this.findLatestPositionBeforeTime(
          otherHistory, currPos.time());

      if (otherPositionBeforeCurrOpt.isEmpty()) {
        continue;
      }

      ShipPosition otherPositionBeforeCurr = otherPositionBeforeCurrOpt.get();
      VelocityVector otherVelocityVector = otherHistory.size() >= 2
          ? velocityService.calculateVelocityVector(
          otherHistory.get(otherHistory.size() - 2), otherPositionBeforeCurr)
          : new VelocityVector(0, 0);

      for (int t = 1; t <= 60; t++) {
        double predictedCurrX = currPos.x() + ownVelocityVector.vx() * t;
        double predictedCurrY = currPos.y() + ownVelocityVector.vy() * t;

        double predictedOtherX = otherPositionBeforeCurr.x() + otherVelocityVector.vx() * t;
        double predictedOtherY = otherPositionBeforeCurr.y() + otherVelocityVector.vy() * t;

        double distance = Math.hypot(predictedCurrX - predictedOtherX,
            predictedCurrY - predictedOtherY);

        if (distance < 1.0) {
          return ThreatStatus.RED;
        } else if (distance == 1.0 && maxThreat == ThreatStatus.GREEN) {
          maxThreat = ThreatStatus.YELLOW;
        }
      }
    }

    return maxThreat;
  }

  private Optional<ShipPosition> findLatestPositionBeforeTime(List<ShipPosition> positions, long time) {
    return positions.stream()
        .filter(p -> p.time() <= time)
        .max(Comparator.comparingLong(ShipPosition::time));
  }
}
