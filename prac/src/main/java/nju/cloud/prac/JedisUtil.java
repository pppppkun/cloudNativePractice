package nju.cloud.prac;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {

    private static JedisPool pool = null;

    public static JedisPool getJedisPool() {
        if (pool == null) {
            String ip = "172.29.4.47";
            int port = 35999;
            String password = "123456";
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(18);
            jedisPoolConfig.setMaxIdle(8);
            pool = new JedisPool(jedisPoolConfig, ip, port, 10000, password);
        }
        return pool;
    }
    
}