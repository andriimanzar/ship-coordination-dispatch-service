package com.challenge.ship.coordination.dispatchservice.dto;

import java.util.List;

public record ShipHistoryResponse(String id, List<ShipHistoryPosition> positions) {

}
