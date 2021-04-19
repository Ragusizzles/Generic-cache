package com.hid.cache;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.Properties;

import org.json.simple.JSONObject;

public class MemcachedJava {

	public static void main(String[] args) {

		// Connecting to Memcached server on localhost
		/*
		 * MemcachedClient mcc = null; 
		 * 
		 * try { 
		 * 
		 * mcc = new MemcachedClient(new
		 * InetSocketAddress("127.0.0.1", 11211)); 
		 * 
		 * } 
		 * catch (IOException e) {
		 * 
		 * System.out.println("Exception has been caught -" + e.getMessage()); 
		 * 
		 * }
		 * 
		 * System.out.println("Connection to server sucessfully");
		 * 
		 * // set data into memcached server
		 * System.out.println("set status:"+mcc.set("key", 900, "Ragu").isDone());
		 * 
		 * //Get value from cache 
		 * 
		 * System.out.println("Get from Cache:"+mcc.get("key"));
		 */

		
		JSONObject obj = new JSONObject();
		obj.put("name", "foo");
		obj.put("num", new Integer(100));
		obj.put("balance", new Double(1000.21));
		obj.put("is_vip", new Boolean(true));

		System.out.println("Input JSON - "+obj);

		Properties properties = new Properties();

		String maxEntries ="";                  // Global property to obtain maximum number of entries.
		String maxTimeout="";					// Global property to obtain maximum timeout for cache entries.
		try {

			FileInputStream ipStr = new FileInputStream(System.getProperty("user.dir") + File.separator + "config.properties");
			properties.load(ipStr);
			// Load the property for 'maxEntry' and 'Global timeout' from properties file.
			maxEntries = Optional.ofNullable(properties.getProperty("maxEntries")).orElse("100");
			maxTimeout = Optional.ofNullable(properties.getProperty("maxTimeout")).orElse("1000");

		}catch(Exception e) {
			System.out.println();
		}
		System.out.println("Max entries = " + maxEntries);
		System.out.println("Max Timeout = " + maxTimeout);
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		
		Long globalTimeOut = Long.parseLong(maxTimeout);
		Integer maxEntry = Integer.parseInt(maxEntries);
		CacheManager<String,JSONObject> gc = new CacheManager<String, JSONObject>(globalTimeOut, maxEntry);
		Optional<Long> ttl = Optional.of(500L);
		gc.addEntry("users/Ragu",obj, ttl);
		gc.addEntry("users/Ram",obj, Optional.empty());

		System.out.println("Resukt ->"+gc.get("users/Ragu"));
		System.out.println("Resukt ->"+gc.get("users/Ram"));

	}

}
