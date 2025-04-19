package com.challenge.ship.coordination.dispatchservice.service;

import com.challenge.ship.coordination.dispatchservice.dto.AllShipsStatusResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipHistoryPosition;
import com.challenge.ship.coordination.dispatchservice.dto.ShipHistoryResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipPositionRequest;
import com.challenge.ship.coordination.dispatchservice.dto.ShipStatusResponse;
import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;
import com.challenge.ship.coordination.dispatchservice.repository.ShipRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {

  private final ShipRepository shipRepository;
  private final VelocityService velocityService;
  private final CollisionDetectorService collisionDetectorService;

  @Override
  public AllShipsStatusResponse getCurrentFleetStatus() {
    List<ShipStatusResponse> snapshots = shipRepository.getAllShipsPositions()
        .entrySet()
        .stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .map(entry -> new ShipStatusResponse(entry.getKey(), entry.getValue().getLast()))
        .toList();

    return new AllShipsStatusResponse(snapshots);
  }

  @Override
  public ShipHistoryResponse getShipPositionHistory(String id) {
    List<ShipHistoryPosition> historyPositions = shipRepository.getShipPositions(id)
        .stream()
        .map(ShipHistoryPosition::new)
        .toList();

    return new ShipHistoryResponse(id, historyPositions);
  }

  @Override
  public ShipPosition submitPosition(String shipId,
      ShipPositionRequest positionRequest) {
    List<ShipPosition> positionsHistory = shipRepository.getShipPositions(shipId);
    if (!positionsHistory.isEmpty() &&
        positionRequest.time() <=
            positionsHistory.get(positionsHistory.size() - 1).time()) {
      throw new IllegalArgumentException("time out of range");
    }

    int speed = 0;
    if (positionsHistory.size() > 1) {
      speed = velocityService.calculateSpeed(positionsHistory.get(positionsHistory.size() - 1),
          positionRequest);
    }

    ThreatStatus threatStatus = collisionDetectorService.assesThreat(shipId, positionRequest);

    ShipPosition position = new ShipPosition(positionRequest, speed, threatStatus);

    shipRepository.addPosition(shipId, position);

    return position;
  }

  @Override
  public void flushData() {
    shipRepository.clear();
  }
}
