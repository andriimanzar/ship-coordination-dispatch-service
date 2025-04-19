package com.challenge.ship.coordination.dispatchservice.controller.v1;

import com.challenge.ship.coordination.dispatchservice.dto.AllShipsStatusResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipHistoryResponse;
import com.challenge.ship.coordination.dispatchservice.dto.ShipPositionRequest;
import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.service.ShipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/ships")
@RequiredArgsConstructor
public class ShipController {

  private final ShipService shipService;

  @GetMapping
  public ResponseEntity<AllShipsStatusResponse> getCurrentFleetStatus() {
    return ResponseEntity.ok(shipService.getCurrentFleetStatus());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ShipHistoryResponse> getShipHistory(@PathVariable String id) {
    return ResponseEntity.ok(shipService.getShipPositionHistory(id));
  }

  @PostMapping("/{id}/position")
  public ResponseEntity<ShipPosition> submitPosition(@PathVariable String id,
      @RequestBody ShipPositionRequest request) {
    ShipPosition shipPosition = shipService.submitPosition(id, request);
    return new ResponseEntity<>(shipPosition, HttpStatus.CREATED);
  }
}
