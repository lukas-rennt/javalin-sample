package de.lukas.web;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class DingStoreRedis implements DingStore {

  private final static String DINGE_KEY = "dinge";

  private final static Gson GSON = new Gson();

  private final JedisPool jedisPool;

  private static JedisPoolConfig getJedisPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
  }

  public DingStoreRedis( //
      final String redisHost, //
      final int redisPort) {
    jedisPool = new JedisPool( //
        getJedisPoolConfig(), //
        redisHost, //
        redisPort);
  }

  @Override
  public Collection<Ding> getAll() {
    Collection<String> values;
    try (final Jedis jedis = jedisPool.getResource()) {
      values = jedis.hvals(DINGE_KEY);
    }

    return values == null ? //
        Collections.emptyList() : //
        values.stream() //
            .map((dingJson) -> GSON.fromJson(dingJson, Ding.class)) //
            .collect(Collectors.toList());
  }

  @Override
  public Ding get(final int id) {
    String value;
    try (final Jedis jedis = jedisPool.getResource()) {
      value = jedis.hget(DINGE_KEY, Integer.toString(id));
    }

    return Optional.ofNullable(value) //
        .map((dingJson) -> GSON.fromJson(dingJson, Ding.class)) //
        .orElse(null);
  }

  @Override
  public void put(final Ding ding) {
    if (ding == null) {
      throw new IllegalArgumentException("ding darf nicht null sein");
    }

    final String value = GSON.toJson(ding);

    try (final Jedis jedis = jedisPool.getResource()) {
      jedis.hset(DINGE_KEY, Integer.toString(ding.getId()), value);
    }
  }

  @Override
  public void remove(final int id) {
    try (final Jedis jedis = jedisPool.getResource()) {
      jedis.hdel(DINGE_KEY, Integer.toString(id));
    }
  }

}
