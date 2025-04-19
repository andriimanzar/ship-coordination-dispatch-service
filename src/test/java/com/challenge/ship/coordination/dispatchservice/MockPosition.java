package com.challenge.ship.coordination.dispatchservice;

import com.challenge.ship.coordination.dispatchservice.model.TemporalPosition;

public record MockPosition(long time, int x, int y) implements TemporalPosition {

}
