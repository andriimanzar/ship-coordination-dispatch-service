package com.challenge.ship.coordination.dispatchservice.controller.v1;

import com.challenge.ship.coordination.dispatchservice.service.ShipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class SystemController {

  private final ShipService shipService;

  @PostMapping("/flush")
  public ResponseEntity<Void> flushAllData() {
    shipService.flushData();
    return ResponseEntity.ok().build();
  }

}
