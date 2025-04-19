package com.challenge.ship.coordination.dispatchservice.repository.redis;

import com.challenge.ship.coordination.dispatchservice.model.ShipPosition;
import com.challenge.ship.coordination.dispatchservice.repository.ShipRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisShipRepository implements ShipRepository {

  private static final String SHIP_PREFIX = "ship:";
  private static final String POSITION_SUFFIX = ":positions";

  private final RedisTemplate<String, ShipPosition> redisTemplate;

  public Map<String, List<ShipPosition>> getAllShipsPositions() {
    Map<String, List<ShipPosition>> result = new HashMap<>();

    ScanOptions options = ScanOptions.scanOptions()
        .match(SHIP_PREFIX + "*:positions")
        .count(100)
        .build();

    try (Cursor<String> cursor = redisTemplate.scan(options)) {
      while (cursor.hasNext()) {
        String key = cursor.next();

        String shipId = this.extractShipIdFromKey(key);
        List<ShipPosition> positions = redisTemplate.opsForList().range(key, 0, -1);

        result.put(shipId, positions);
      }
    }

    return result;
  }

  public Optional<List<ShipPosition>> getShipPositions(String shipId) {
    String key = this.getShipKey(shipId);
    List<ShipPosition> positions = redisTemplate.opsForList().range(key, 0, -1);

    return Optional.of(positions)
        .filter(list -> !list.isEmpty());
  }

  public synchronized void submitPosition(String shipId, ShipPosition shipPosition) {
    String key = this.getShipKey(shipId);
    redisTemplate.opsForList().rightPush(key, shipPosition);
  }

  private String getShipKey(String shipId) {
    return SHIP_PREFIX + shipId + POSITION_SUFFIX;
  }

  private String extractShipIdFromKey(String key) {
    return key.replace(SHIP_PREFIX, "").replace(POSITION_SUFFIX, "");
  }
}
