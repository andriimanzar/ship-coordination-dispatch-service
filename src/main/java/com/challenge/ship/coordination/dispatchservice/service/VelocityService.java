package com.challenge.ship.coordination.dispatchservice.service;

import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;
import com.challenge.ship.coordination.dispatchservice.model.VelocityVector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VelocityService {

  private static final int MIN_SPEED = 0;
  private static final int MAX_SPEED = 100;

  public int calculateSpeed(TemporalPosition prev,
      TemporalPosition curr) {
    VelocityVector velocityVector = this.calculateVelocityVector(prev, curr);

    double speed = Math.hypot(velocityVector.vx(), velocityVector.vy());

    if (speed < MIN_SPEED || speed > MAX_SPEED) {
      throw new IllegalArgumentException("speed is out of bounds (0-100 cells/second)");
    }

    return (int) Math.round(speed);
  }

  public VelocityVector calculateVelocityVector(TemporalPosition prev, TemporalPosition curr) {
    int timeDelta = curr.time() - prev.time();
    if (timeDelta == 0) {
      return new VelocityVector(0, 0);
    } else if (timeDelta < 0) {
      throw new IllegalArgumentException("time delta is negative");
    }

    double vx = ((double) (curr.x() - prev.x())) / timeDelta;
    double vy = ((double) (curr.y() - prev.y())) / timeDelta;

    return new VelocityVector(vx, vy);
  }
}
