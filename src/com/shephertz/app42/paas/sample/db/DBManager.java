package com.shephertz.app42.paas.sample.db;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.shephertz.app42.paas.sample.util.Util;

public class DBManager {

	JedisPool pool = null;
	private Jedis jedis = null;

	/*
	* initializes redis with the given credentials
	*/
	
	public DBManager() {
		// db name by default is set to 0
		String dbIp = Util.getDBIp();
		String password = Util.getDBPassword();
		int port = Util.getDBPort();
		pool = new JedisPool(new JedisPoolConfig(), dbIp, port);
		jedis = pool.getResource();
		jedis.auth(password);
		try {
			jedis.connect();
		} catch (JedisConnectionException e) {
			System.out.println("Unable to connect on " + dbIp + ":" + port);
			pool.returnBrokenResource(jedis);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/*
	* push data into redis 
	*/
	
	public void insert(String name) {
		jedis.lpush("username", name);
		jedis.save();
		jedis.disconnect();
	}

	/*
	* get all data from redis 
	*/
	
	public List<String> select(String key) throws Exception {
		List<String> list = new ArrayList<String>();
		try {
			list = jedis.lrange(key, 0, 1000);
		} catch (Exception e) {
			list = jedis.lrange("username", 0, 1000);
		}
		jedis.disconnect();
		return list;
	}
}
