package com.itheima.utils;

import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtils2 {
    private static JedisPool pool =null;
    static {
    	ResourceBundle resource = ResourceBundle.getBundle("redis");
    	JedisPoolConfig poolConfig = new JedisPoolConfig();
    	poolConfig.setMaxIdle(Integer.parseInt(resource.getString("redis.maxIdle")));
		poolConfig.setMinIdle(Integer.parseInt((resource.getString("redis.minIdle"))));
		poolConfig.setMaxTotal(Integer.parseInt((resource.getString("redis.maxTotal"))));
		pool = new JedisPool(poolConfig,(resource.getString("redis.url")),Integer.valueOf((resource.getString("redis.port"))));
    }
    public static Jedis getJedis() {
    	return pool.getResource();
    }
    public static void release(Jedis jedis) {
    	jedis.close();
    }
    public static void main(String[] args) {
		Jedis jedis = JedisUtils2.getJedis();
		System.out.println(jedis.get("xxxx"));
	}
}
