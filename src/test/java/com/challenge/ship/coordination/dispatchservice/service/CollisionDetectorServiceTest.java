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
import java.util.List;
import java.util.Map;
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
  @DisplayName("assesThreat when less than two ships registered returns GREEN")
  void assesThreat_whenLessThanTwoShips_returnsGreen() {
    String shipId = "ship123";
    TemporalPosition currPosition = new MockPosition(1000, 10, 10);
    ShipPosition ownPos = new ShipPosition(currPosition, 0, ThreatStatus.GREEN);

    when(shipRepository.getShipPositions(shipId))
        .thenReturn(Optional.of(List.of(ownPos)));

    when(shipRepository.getAllShipsPositions())
        .thenReturn(Map.of(shipId, List.of(ownPos)));

    ThreatStatus result = collisionDetectorService.assesThreat(shipId, currPosition);

    assertEquals(ThreatStatus.GREEN, result);
  }

  @Test
  @Order(2)
  @DisplayName("assesThreat when other ships have no positions before curr returns GREEN")
  void assesThreat_whenOtherShipsHaveNoValidHistory_returnsGreen() {
    String shipId = "ship123";
    TemporalPosition curr = new MockPosition(1000, 10, 10);
    ShipPosition own = new ShipPosition(curr, 0, ThreatStatus.GREEN);

    when(shipRepository.getShipPositions(shipId))
        .thenReturn(Optional.of(List.of(own, own)));

    TemporalPosition future = new MockPosition(1500, 20, 20);
    ShipPosition futurePos = new ShipPosition(future, 0, ThreatStatus.GREEN);

    when(shipRepository.getAllShipsPositions())
        .thenReturn(Map.of("ship999", List.of(futurePos)));

    ThreatStatus result = collisionDetectorService.assesThreat(shipId, curr);

    assertEquals(ThreatStatus.GREEN, result);
  }

  @Test
  @Order(3)
  @DisplayName("assesThreat when collision predicted returns RED")
  void assesThreat_whenCollisionPredicted_returnsRed() {
    String shipId = "ship123";
    TemporalPosition curr = new MockPosition(1000, 10, 10);
    ShipPosition ownPrev = new ShipPosition(new MockPosition(900, 9, 10), 0, ThreatStatus.GREEN);
    ShipPosition ownCurr = new ShipPosition(curr, 0, ThreatStatus.GREEN);

    when(shipRepository.getShipPositions(shipId))
        .thenReturn(Optional.of(List.of(ownPrev, ownCurr)));

    ShipPosition otherPrev = new ShipPosition(new MockPosition(900, 11, 10), 0, ThreatStatus.GREEN);
    ShipPosition otherCurr = new ShipPosition(new MockPosition(1000, 12, 10), 0, ThreatStatus.GREEN);

    when(shipRepository.getAllShipsPositions())
        .thenReturn(Map.of("ship456", List.of(otherPrev, otherCurr)));

    when(velocityService.calculateVelocityVector(any(), any()))
        .thenReturn(new VelocityVector(1, 0))
        .thenReturn(new VelocityVector(-1, 0));

    ThreatStatus result = collisionDetectorService.assesThreat(shipId, curr);

    assertEquals(ThreatStatus.RED, result);
  }

  @Test
  @Order(4)
  @DisplayName("assesThreat when ships approach to exactly distance 1 returns YELLOW")
  void assesThreat_whenShipsApproachToOne_returnsYellow() {
    String shipId = "ship123";
    TemporalPosition curr = new MockPosition(1000, 10, 10);
    ShipPosition ownPrev = new ShipPosition(new MockPosition(900, 9, 10), 0, ThreatStatus.GREEN);
    ShipPosition ownCurr = new ShipPosition(curr, 0, ThreatStatus.GREEN);

    when(shipRepository.getShipPositions(shipId))
        .thenReturn(Optional.of(List.of(ownPrev, ownCurr)));

    ShipPosition otherPrev = new ShipPosition(new MockPosition(900, 13, 10), 0, ThreatStatus.GREEN);
    ShipPosition otherCurr = new ShipPosition(new MockPosition(1000, 13, 10), 0, ThreatStatus.GREEN);

    when(shipRepository.getAllShipsPositions())
        .thenReturn(Map.of("ship456", List.of(otherPrev, otherCurr)));

    when(velocityService.calculateVelocityVector(any(), any()))
        .thenReturn(new VelocityVector(1, 0))
        .thenReturn(new VelocityVector(-1, 0));

    ThreatStatus result = collisionDetectorService.assesThreat(shipId, curr);

    assertEquals(ThreatStatus.YELLOW, result);
  }
}