package com.challenge.ship.coordination.dispatchservice.dto;

import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;

public record ShipPositionRequest(long time, int x, int y) implements TemporalPosition {

}
