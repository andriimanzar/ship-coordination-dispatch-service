package com.challenge.ship.coordination.dispatchservice.repository.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.challenge.ship.coordination.dispatchservice.MockPosition;
import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.model.ThreatStatus;
import com.challenge.ship.coordination.dispatchservice.repository.ShipRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@Testcontainers
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("RedisShipRepository integration tests")
class RedisShipRepositoryTest {

  @Container
  @SuppressWarnings("unused")
  private static final GenericContainer<?> redis = new GenericContainer<>("redis:8.0-rc1-alpine")
      .withExposedPorts(6379);

  @Autowired
  private ShipRepository shipRepository;
  @Autowired
  private RedisTemplate<String, ShipPosition> redisTemplate;


  @BeforeEach
  void setUp() {
    redisTemplate.getConnectionFactory().getConnection()
        .serverCommands()
        .flushAll();
  }

  @Test
  @DisplayName("getAllShipsPositions when multiple ships have submitted positions must return map with their positions")
  @Order(1)
  void getAllShipsPositions_whenMultipleShipsHaveSubmittedPositions_thenReturnsExpectedMap() {
    String shipId1 = "ship123";
    ShipPosition shipPosition1 = new ShipPosition(new MockPosition(1000, 10, 10), 5,
        ThreatStatus.GREEN);
    shipRepository.submitPosition(shipId1, shipPosition1);

    String shipId2 = "ship789";
    ShipPosition shipPosition2 = new ShipPosition(new MockPosition(1050, 20, 20), 10,
        ThreatStatus.YELLOW);
    shipRepository.submitPosition(shipId2, shipPosition2);

    Map<String, List<ShipPosition>> allShipsPositions = shipRepository.getAllShipsPositions();

    assertNotNull(allShipsPositions, "Expected a map of ships and positions");
    assertTrue(allShipsPositions.containsKey(shipId1), "Expected ship123 to be in the map");
    assertTrue(allShipsPositions.containsKey(shipId2), "Expected ship789 to be in the map");
    assertEquals(1, allShipsPositions.get(shipId1).size(), "Expected 1 position for ship123");
    assertEquals(1, allShipsPositions.get(shipId2).size(), "Expected 1 position for ship789");
  }

  @Test
  @DisplayName("getShipPositions when no positions submitted must return empty optional")
  @Order(2)
  void getShipPositions_whenNoPositionsSubmitted_returnsEmptyOpt() {
    Optional<List<ShipPosition>> shipPositions = shipRepository.getShipPositions("nonExistentShip");
    assertFalse(shipPositions.isPresent(), "Expected no positions for a non-existent ship");
  }

  @Test
  @DisplayName("getShipPositions when ship has submitted positions must return list of positions")
  @Order(3)
  void getShipPositions_whenNoPositionsSubmitted_returnsListOfPositions() {
    String shipId = "ship123";
    ShipPosition shipPosition1 = new ShipPosition(new MockPosition(1000, 10, 10), 5,
        ThreatStatus.GREEN);
    shipRepository.submitPosition(shipId, shipPosition1);

    Optional<List<ShipPosition>> shipPositions = shipRepository.getShipPositions(shipId);

    assertTrue(shipPositions.isPresent(), "Expected positions for an existing ship");
    assertEquals(1, shipPositions.get().size(), "Expected 1 position in the list");
    assertEquals(shipPosition1, shipPositions.get().get(0), "Expected the correct ship position");
  }

  @Test
  @DisplayName("submitPosition when position submitted must store it for given ship")
  @Order(4)
  void submitPosition_whenPositionSubmitted_thenPositionStored() {
    String shipId = "ship456";
    ShipPosition shipPosition = new ShipPosition(new MockPosition(1050, 20, 20), 10,
        ThreatStatus.YELLOW);

    shipRepository.submitPosition(shipId, shipPosition);

    Optional<List<ShipPosition>> shipPositions = shipRepository.getShipPositions(shipId);

    assertTrue(shipPositions.isPresent(), "Expected positions to exist after submission");
    assertEquals(1, shipPositions.get().size(), "Expected 1 position in the list");
    assertEquals(shipPosition, shipPositions.get().get(0),
        "Expected the correct ship position to be stored");
  }
}