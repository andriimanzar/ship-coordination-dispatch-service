package com.challenge.ship.coordination.dispatchservice.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ThreatStatus {

  GREEN, YELLOW, RED;

  @JsonValue
  public String toLowerCase() {
    return this.name().toLowerCase();
  }
}
