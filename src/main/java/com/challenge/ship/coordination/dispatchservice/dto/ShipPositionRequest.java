package com.challenge.ship.coordination.dispatchservice.dto;

import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;

public record ShipPositionRequest(int time, int x, int y) implements TemporalPosition {

}
