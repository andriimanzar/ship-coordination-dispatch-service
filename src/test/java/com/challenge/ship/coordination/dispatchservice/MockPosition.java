package com.challenge.ship.coordination.dispatchservice;

import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;

public record MockPosition(int time, int x, int y) implements TemporalPosition {

}
