package com.weather.spark.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;

/**
 * Redis 写入工具
 * 所有 Key 自动设置 300 秒过期
 */
public class RedisWriter {

    private static final String REDIS_HOST = "192.168.139.38";
    private static final int REDIS_PORT = 6379;
    private static final int TTL_SECONDS = 300;

    private static JedisPool jedisPool;

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        jedisPool = new JedisPool(config, REDIS_HOST, REDIS_PORT);
    }

    /**
     * 写入单个键值对
     */
    public static void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            jedis.expire(key, TTL_SECONDS);
        } catch (Exception e) {
            System.err.println("Redis 写入失败: " + e.getMessage());
        }
    }

    /**
     * 写入 Hash
     */
    public static void hset(String key, String field, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, field, value);
            jedis.expire(key, TTL_SECONDS);
        } catch (Exception e) {
            System.err.println("Redis HSET 失败: " + e.getMessage());
        }
    }

    /**
     * 写入 ZSet（有序集合）
     */
    public static void zadd(String key, double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(key, score, member);
            jedis.expire(key, TTL_SECONDS);
        } catch (Exception e) {
            System.err.println("Redis ZADD 失败: " + e.getMessage());
        }
    }

    /**
     * 删除 Key
     */
    public static void del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {}
    }

    /**
     * 批量写入 Hash（覆盖）
     */
    public static void hsetAll(String key, Map<String, String> data) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, data);
            jedis.expire(key, TTL_SECONDS);
        } catch (Exception e) {
            System.err.println("Redis HSET 批量失败: " + e.getMessage());
        }
    }
}
