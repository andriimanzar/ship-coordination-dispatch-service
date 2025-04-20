# Ship Coordination Dispatch Service

## Overview

This service was developed as part of a back-end challenge. It tracks ship positions and calculates collision risks. The system analyzes trajectories and speeds to provide safety warnings using a color-coded risk assessment system.

## Features

- Position tracking for ships
- Collision risk assessment with three levels (green, yellow, red)
- Historical position data storage

## Technologies used

The solution uses a containerized approach with Docker:
- Java 21
- Spring Boot
- Redis for position storage
- TestContainers for integration testing
- Docker for containerization

## Getting Started

### Running an app

Launch the service with a single command:
`docker compose up`

The API will be available at `http://localhost:8080`.

## API Endpoints

### Position Reporting

`POST /v1/api/ships/:id/position`
- Report ship position at a specific time
- Returns collision status (green, yellow, red)

### Ship Status

`GET /v1/api/ships`
- Retrieve status of all tracked ships

### Ship History

`GET /v1/api/ships/:id`
- Get position history for a specific ship

### Reset Data

`POST /v1/api/flush`
- Clear all data (for testing purposes)
