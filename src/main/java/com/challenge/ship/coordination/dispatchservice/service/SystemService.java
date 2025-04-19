package com.challenge.ship.coordination.dispatchservice.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemService {

  private final RedisTemplate<String, Object> redisTemplate;

  public void flushAllData() {
    Optional.of(redisTemplate.getConnectionFactory())
        .map(RedisConnectionFactory::getConnection)
        .ifPresent(connection -> connection.serverCommands().flushAll());
  }
}
