package com.challenge.ship.coordination.dispatchservice.service;

import com.challenge.ship.coordination.dispatchservice.dto.AllShipsStatusResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipHistoryPosition;
import com.challenge.ship.coordination.dispatchservice.dto.ShipHistoryResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipPositionRequest;
import com.challenge.ship.coordination.dispatchservice.dto.ShipStatusResponse;
import com.challenge.ship.coordination.dispatchservice.exception.ShipNotFoundException;
import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;
import com.challenge.ship.coordination.dispatchservice.repository.ShipRepository;
import com.challenge.ship.coordination.dispatchservice.validation.ShipPositionRequestValidator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipServiceImpl implements ShipService {

  private final ShipRepository shipRepository;
  private final ShipPositionRequestValidator shipPositionRequestValidator;
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
  public ShipHistoryResponse getShipPositionHistory(String shipId) {
    List<ShipPosition> shipPositions = shipRepository.getShipPositions(shipId)
        .orElseThrow(() -> new ShipNotFoundException("ship was not found"));

    List<ShipHistoryPosition> historyPositions = shipPositions
        .stream()
        .map(ShipHistoryPosition::new)
        .toList();

    return new ShipHistoryResponse(shipId, historyPositions);
  }

  @Override
  public ShipPosition submitPosition(String shipId,
      ShipPositionRequest positionRequest) {
    shipPositionRequestValidator.validateCoordinates(positionRequest.x(), positionRequest.y());
    shipPositionRequestValidator.validateTimeIsNotGreaterThanCurrent(positionRequest.time());

    List<ShipPosition> positionsHistory = shipRepository.getShipPositions(shipId)
        .orElse(new ArrayList<>());
    if (!positionsHistory.isEmpty()) {
      shipPositionRequestValidator.validateTimeIsGreaterThanThePreviousReceived(
          positionRequest.time(),
          positionsHistory.getLast().time());
    }

    int speed = 0;
    if (positionsHistory.size() > 1) {
      speed = velocityService.calculateSpeed(positionsHistory.getLast(),
          positionRequest);
    }

    ThreatStatus threatStatus = collisionDetectorService.assesThreat(shipId, positionRequest);
    ShipPosition position = new ShipPosition(positionRequest, speed, threatStatus);

    shipRepository.submitPosition(shipId, position);

    return position;
  }
}
