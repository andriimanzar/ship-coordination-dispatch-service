package com.challenge.ship.coordination.dispatchservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.challenge.ship.coordination.dispatchservice.MockPosition;
import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;
import com.challenge.ship.coordination.dispatchservice.model.VelocityVector;
import com.challenge.ship.coordination.dispatchservice.repository.ShipRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("CollisionDetectorService unit tests")
class CollisionDetectorServiceTest {

  @Mock
  private ShipRepository shipRepository;
  @Mock
  private VelocityService velocityService;
  @InjectMocks
  private CollisionDetectorService collisionDetectorService;

  @Test
  @Order(1)
  @DisplayName("assesThreat when less than two ships registered return GREEN")
  void assesThreat_whenShipHistoryLessThanTwo_returnsGreen() {
    String shipId = "ship123";
    MockPosition currPosition = new MockPosition(1000, 10, 10);

    when(shipRepository.getShipPositions(shipId))
        .thenReturn(Optional.of(
            Collections.singletonList(new ShipPosition(currPosition, 0, ThreatStatus.GREEN))));

    ThreatStatus threatStatus = collisionDetectorService.assesThreat(shipId, currPosition);

    assertEquals(ThreatStatus.GREEN, threatStatus);
  }

  @Test
  @Order(2)
  @DisplayName("assesThreat when no other ships have sufficient position history must return GREEN")
  void assesThreat_whenNoOtherShipHasSufficientHistory_returnsGreen() {
    String shipId = "ship123";
    TemporalPosition curr = new MockPosition(1000, 10, 10);

    ShipPosition pos = new ShipPosition(curr, 0, ThreatStatus.GREEN);

    when(shipRepository.getShipPositions(shipId))
        .thenReturn(Optional.of(Arrays.asList(pos, pos)));
    when(shipRepository.getAllShipsPositions())
        .thenReturn(Collections.singletonMap("ship456",
            List.of(new ShipPosition(curr, 0, ThreatStatus.GREEN))));

    ThreatStatus threatStatus = collisionDetectorService.assesThreat(shipId, curr);

    assertEquals(ThreatStatus.GREEN, threatStatus);
  }

  @Test
  @Order(3)
  @DisplayName("assesThreat when collision predicted must return RED")
  void assesThreat_whenCollisionPredicted_returnsRed() {
    String shipId = "ship123";
    TemporalPosition curr = new MockPosition(1000, 10, 10);
    ShipPosition ownPosition = new ShipPosition(curr, 0, ThreatStatus.GREEN);

    when(shipRepository.getShipPositions(shipId))
        .thenReturn(Optional.of(Arrays.asList(ownPosition, ownPosition)));

    TemporalPosition otherCurr = new MockPosition(1000, 10, 10);
    ShipPosition otherShipPosition = new ShipPosition(otherCurr, 0, ThreatStatus.GREEN);
    when(shipRepository.getAllShipsPositions())
        .thenReturn(Collections.singletonMap("ship456", Arrays.asList(otherShipPosition, otherShipPosition)));

    VelocityVector ownVelocityVector = new VelocityVector(1, 0);
    VelocityVector otherVelocityVector = new VelocityVector(1, 0);
    when(velocityService.calculateVelocityVector(any(), any())).thenReturn(ownVelocityVector)
        .thenReturn(otherVelocityVector);

    ThreatStatus threatStatus = collisionDetectorService.assesThreat(shipId, curr);

    assertEquals(ThreatStatus.RED, threatStatus);
  }

  @Test
  @Order(4)
  @DisplayName("assesThreat when ships are approaching but not colliding must return YELLOW")
  void assesThreat_whenShipsApproachingButNotColliding_returnsYellow() {
    String shipId = "ship123";
    TemporalPosition curr = new MockPosition(1000, 10, 10);
    ShipPosition ownPosition = new ShipPosition(curr, 0, ThreatStatus.GREEN);

    when(shipRepository.getShipPositions(shipId))
        .thenReturn(Optional.of(Arrays.asList(ownPosition, ownPosition)));

    TemporalPosition otherCurr = new MockPosition(1000, 10, 11);
    ShipPosition otherShipPosition = new ShipPosition(otherCurr, 0, ThreatStatus.GREEN);
    when(shipRepository.getAllShipsPositions())
        .thenReturn(Collections.singletonMap("ship456", Arrays.asList(otherShipPosition, otherShipPosition)));

    VelocityVector ownVelocityVector = new VelocityVector(1, 0);
    VelocityVector otherVelocityVector = new VelocityVector(1, 0);
    when(velocityService.calculateVelocityVector(any(), any()))
        .thenReturn(ownVelocityVector)
        .thenReturn(otherVelocityVector);

    ThreatStatus threatStatus = collisionDetectorService.assesThreat(shipId, curr);

    assertEquals(ThreatStatus.YELLOW, threatStatus);
  }
}