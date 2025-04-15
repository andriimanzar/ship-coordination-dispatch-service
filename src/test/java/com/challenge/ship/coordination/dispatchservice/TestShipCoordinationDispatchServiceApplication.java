package com.challenge.ship.coordination.dispatchservice;

import org.springframework.boot.SpringApplication;

public class TestShipCoordinationDispatchServiceApplication {

  public static void main(String[] args) {
    SpringApplication.from(ShipCoordinationDispatchServiceApplication::main)
        .with(TestcontainersConfiguration.class).run(args);
  }

}
