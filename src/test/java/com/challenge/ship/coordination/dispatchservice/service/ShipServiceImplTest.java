package com.challenge.ship.coordination.dispatchservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.challenge.ship.coordination.dispatchservice.MockPosition;
import com.challenge.ship.coordination.dispatchservice.dto.AllShipsStatusResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipHistoryResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipPositionRequest;
import com.challenge.ship.coordination.dispatchservice.dto.ShipStatusResponse;
import com.challenge.ship.coordination.dispatchservice.exception.ShipNotFoundException;
import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;
import com.challenge.ship.coordination.dispatchservice.repository.ShipRepository;
import com.challenge.ship.coordination.dispatchservice.validation.ShipPositionRequestValidator;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ShipServiceImpl unit tests")
class ShipServiceImplTest {

  @Mock
  private ShipRepository shipRepository;
  @Mock
  private VelocityService velocityService;
  @Mock
  private CollisionDetectorService collisionDetectorService;
  @Mock
  private ShipPositionRequestValidator shipPositionRequestValidator;
  @InjectMocks
  private ShipServiceImpl shipService;

  @Test
  @DisplayName("getCurrentFleetStatus when ships exist must return last positions")
  void getCurrentFleetStatus_whenShipsExists_returnsLastPositions() {
    ShipPosition position1 = new ShipPosition(new MockPosition(1000, 1, 1), 5, ThreatStatus.GREEN);
    ShipPosition position2 = new ShipPosition(new MockPosition(2000, 2, 2), 10, ThreatStatus.YELLOW);

    when(shipRepository.getAllShipsPositions())
        .thenReturn(
            Map.of("ship1", List.of(position1),
                "ship2", List.of(position2)));

    AllShipsStatusResponse response = shipService.getCurrentFleetStatus();

    ShipStatusResponse ship1ExpectedStatusResponse = new ShipStatusResponse("ship1", position1);
    ShipStatusResponse ship2ExpectedStatusResponse = new ShipStatusResponse("ship2", position2);

    assertEquals(2, response.ships().size());
    assertEquals(Set.of(ship1ExpectedStatusResponse, ship2ExpectedStatusResponse),
        new HashSet<>(response.ships()));
  }

  @Test
  @DisplayName("getShipPositionHistory when ship exists must return correct history")
  void getShipPositionHistory_whenShipExists_returnsHistory() {
    ShipPosition position1 = new ShipPosition(new MockPosition(1000, 1, 2), 3, ThreatStatus.GREEN);
    ShipPosition position2 = new ShipPosition(new MockPosition(1020, 4, 5), 4, ThreatStatus.YELLOW);

    when(shipRepository.getShipPositions("ship1"))
        .thenReturn(Optional.of(List.of(position1, position2)));

    ShipHistoryResponse response = shipService.getShipPositionHistory("ship1");

    assertEquals("ship1", response.id());
    assertEquals(2, response.positions().size());
  }

  @Test
  @DisplayName("getShipPositionHistory when ship does not exist must throw exception")
  void getShipPositionHistory_whenShipNotFound_throwsException() {
    when(shipRepository.getShipPositions("missing"))
        .thenReturn(Optional.empty());

    assertThrows(ShipNotFoundException.class, () -> shipService.getShipPositionHistory("missing"));
  }

  @Test
  @DisplayName("submitPosition when first position must return 0 speed")
  void submitPosition_whenFirstPosition_returnsZeroSpeed() {
    ShipPositionRequest newPos = new ShipPositionRequest(1000, 0, 0);
    when(shipRepository.getShipPositions("ship1"))
        .thenReturn(Optional.empty());
    when(collisionDetectorService.assesThreat(eq("ship1"), any()))
        .thenReturn(ThreatStatus.GREEN);

    ShipPosition result = shipService.submitPosition("ship1", newPos);

    assertEquals(0, result.speed());
    assertEquals(ThreatStatus.GREEN, result.status());
    verify(shipRepository).submitPosition(eq("ship1"), any());
  }

  @Test
  @DisplayName("submitPosition when second position submitted must calculate speed")
  void submitPosition_whenSecondPositionSubmitted_calculatesSpeed() {
    ShipPosition oldPos = new ShipPosition(new MockPosition(1000, 1, 2), 0, ThreatStatus.GREEN);
    ShipPositionRequest newPos = new ShipPositionRequest(4, 6, 1010);

    when(shipRepository.getShipPositions("ship1"))
        .thenReturn(Optional.of(List.of(oldPos)));
    when(velocityService.calculateSpeed(oldPos, newPos)).thenReturn(5);
    when(collisionDetectorService.assesThreat(eq("ship1"), any()))
        .thenReturn(ThreatStatus.YELLOW);

    ShipPosition result = shipService.submitPosition("ship1", newPos);

    assertEquals(5, result.speed());
    verify(shipRepository).submitPosition(eq("ship1"), any());
  }

  @Test
  @DisplayName("submitPosition must call coordinates and time validation")
  void submitPosition_validatesRequest() {
    int currTime = (int) Instant.now().getEpochSecond();

    ShipPositionRequest newPos = new ShipPositionRequest(currTime, 10, 20);

    when(shipRepository.getShipPositions("ship1"))
        .thenReturn(Optional.empty());
    when(collisionDetectorService.assesThreat(any(), any()))
        .thenReturn(ThreatStatus.GREEN);

    shipService.submitPosition("ship1", newPos);

    verify(shipPositionRequestValidator).validateCoordinates(10, 20);
    verify(shipPositionRequestValidator).validateTimeIsNotGreaterThanCurrent(currTime);
  }

  @Test
  @DisplayName("submitPosition validation time is not greater than last position")
  void submitPosition_whenTimeIsNotGreaterThanPrevious_callsValidator() {
    int currTime = (int) Instant.now().getEpochSecond();

    ShipPositionRequest newPos = new ShipPositionRequest(currTime, 1, 1);
    ShipPosition oldPos = new ShipPosition(new MockPosition(currTime - 1, 1, 1), 0,
        ThreatStatus.GREEN);

    when(shipRepository.getShipPositions("ship1"))
        .thenReturn(Optional.of(List.of(oldPos)));
    when(velocityService.calculateSpeed(any(), eq(newPos)))
        .thenReturn(3);
    when(collisionDetectorService.assesThreat(any(), any()))
        .thenReturn(ThreatStatus.YELLOW);

    shipService.submitPosition("ship1", newPos);

    verify(shipPositionRequestValidator).validateTimeIsGreaterThanThePreviousReceived(
        currTime, currTime - 1);
  }
}