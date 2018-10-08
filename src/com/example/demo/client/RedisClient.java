package com.example.demo.client;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
* @author 作者 : zhujing
* @version 创建时间：2018年9月4日 下午12:56:26
* 类说明
*/

public class RedisClient {
	
	private final String REDIS_BLACK_LIST_KEY = "redis_black_list_key_%s";
	
	private final String REDIS_WHITE_LIST_KEY = "redis_white_list_key_%s";
	
	private final String REDIS_COUNT_KEY = "redis_count_key_%s";

	private JedisPool pool;
	
	private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	
	public Jedis getJedis() {
		jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setMaxIdle(100);
      pool = new JedisPool(jedisPoolConfig, "118.126.91.157", 6379,0,"Gzds1130",8);
//        pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379,0);
        Jedis jedis = pool.getResource();
        return jedis;
    }

	
	public Boolean getRedisBlack(String userName) {
		Jedis jedis = getJedis();
		Boolean key = jedis.exists(String.format(REDIS_BLACK_LIST_KEY, userName));
		return key;
	}
	
	public Boolean getRedisWhite(String userName) {
		Jedis jedis = getJedis();
		Boolean key = jedis.exists(String.format(REDIS_WHITE_LIST_KEY, userName));
		return key;
	}
	
	public void setRedisBlack(String userName) {
		Jedis jedis = getJedis();
		jedis.set(String.format(REDIS_BLACK_LIST_KEY, userName), userName);
		jedis.expire(String.format(REDIS_BLACK_LIST_KEY, userName),2592000);
	}
	
	public void setRedisWhite(String userName) {
		Jedis jedis = getJedis();
		jedis.set(String.format(REDIS_WHITE_LIST_KEY, userName), userName);
	}
	
	public Boolean setRedisCount(String userName) {
		Jedis jedis = getJedis();
		Boolean key = jedis.exists(String.format(REDIS_COUNT_KEY, userName));
		if(key) {
			jedis.incr(String.format(REDIS_COUNT_KEY, userName));
		}else {
			jedis.incrBy(String.format(REDIS_COUNT_KEY, userName),1);
			jedis.expire(String.format(REDIS_COUNT_KEY, userName), 3600);
		}
		
		String count = jedis.get(String.format(REDIS_COUNT_KEY, userName));
		if(Integer.valueOf(count) >= 50) {
			return true;
		}else {
			return false;
		}
		
	}
}
