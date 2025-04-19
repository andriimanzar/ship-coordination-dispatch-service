package com.challenge.ship.coordination.dispatchservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.challenge.ship.coordination.dispatchservice.MockPosition;
import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;
import com.challenge.ship.coordination.dispatchservice.model.VelocityVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("VelocityService unit tests")
class VelocityServiceTest {

  private VelocityService velocityService;

  @BeforeEach
  void setUp() {
    velocityService = new VelocityService();
  }

  @Test
  @Order(1)
  @DisplayName("calculateVelocityVector must return zero vector when time delta is zero")
  void calculateVelocityVector_whenTimeDeltaZero_returnsZeroVector() {
    TemporalPosition prev = new MockPosition(1000, 0, 0);
    TemporalPosition curr = new MockPosition(1000, 5, 5);

    VelocityVector velocity = velocityService.calculateVelocityVector(prev, curr);

    assertEquals(0.0, velocity.vx());
    assertEquals(0.0, velocity.vy());
  }

  @Test
  @Order(2)
  @DisplayName("calculateVelocityVector must return correct vector for valid inputs")
  void calculateVelocityVector_whenValidInput_returnsCorrectVector() {
    TemporalPosition prev = new MockPosition(0, 0, 1000);
    TemporalPosition curr = new MockPosition(10, 10, 1010);

    VelocityVector velocity = velocityService.calculateVelocityVector(prev, curr);

    assertEquals(1.0, velocity.vx());
    assertEquals(1.0, velocity.vy());
  }

  @Test
  @Order(3)
  @DisplayName("calculateSpeed must return expected value for valid positions")
  void calculateSpeed_whenValidInput_returnsCorrectlyCalculatedSpeed() {
    TemporalPosition prev = new MockPosition(1000, 0, 0);
    TemporalPosition curr = new MockPosition(1010, 30, 40);

    int speed = velocityService.calculateSpeed(prev, curr);

    assertEquals(5, speed);
  }

  @Test
  @DisplayName("calculateSpeed must return 0 when time delta is 0")
  @Order(4)
  void calculateSpeed_whenTimeDeltaIsZero_returnsZeroSpeed() {
    TemporalPosition prev = new MockPosition(1000, 0, 0);
    TemporalPosition curr = new MockPosition(1000, 0, 0);

    int speed = velocityService.calculateSpeed(prev, curr);

    assertEquals(0, speed, "Speed should be zero when time delta is 0");
  }

  @Test
  @DisplayName("calculateSpeed must throw an exception when time delta is negative")
  @Order(5)
  void calculateSpeed_whenTimeDeltaNegative_throwsException() {
    TemporalPosition prev = new MockPosition(1010, 0, 0);
    TemporalPosition curr = new MockPosition(1000, 10, 10);

    assertThrows(IllegalArgumentException.class, () ->
        velocityService.calculateSpeed(prev, curr));
  }

  @Test
  @Order(6)
  @DisplayName("calculateSpeed must throw an exception when speed exceeds allowed bounds")
  void calculateSpeed_whenSpeedOutOfBounds_throwsException() {
    TemporalPosition prev = new MockPosition(1000, 0, 0);
    TemporalPosition curr = new MockPosition(1001, 1000, 1000);

    assertThrows(IllegalArgumentException.class, () ->
        velocityService.calculateSpeed(prev, curr)
    );
  }
}