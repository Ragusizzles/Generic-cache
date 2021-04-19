package com.hid.cache;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Generic class to support caching mechanism.<br><br>
 * 
 * <code>Business Logic</code> :  During cache creation - the timestamp (or creation time) is computed and
 * based on this creation date/time and TTL parameter, we can calculate whether an entry in the cache is expired or not
 * 
 * @author raguramm
 *
 * @param <K>
 * @param <V>
 */
public class CacheManager<K, V> implements IGenericCache<K, V> {	

	/**
	 * The data structure that stores the cache contents in memory
	 */
    protected Map<K, CacheValue<V>> cacheMap;
    protected Long cacheTimeToLive;
    private int maxEntries;

    public CacheManager(Long globalTimeToLive, int maxEntries) {
        this.cacheTimeToLive = globalTimeToLive;
        this.maxEntries = maxEntries;
        this.clear();
    }

    
    protected Set<K> getExpiredKeys() {
        return this.cacheMap.keySet().parallelStream()
                .filter(this::isExpired)
                .collect(Collectors.toSet());
    }
    /**
     * This method is responsible to validate the cache entries for time of expiration. If expired, it would be invalidated or removed from map else it remains intact.
     * @param key - Cache entry
     * @return
     */
    protected boolean isExpired(K key) {
    	Optional<Long> timeToLive =   Optional.ofNullable(Optional.ofNullable(this.cacheMap.get(key)).map(CacheValue::getTimeToLive).orElse(this.cacheTimeToLive));
    	LocalDateTime expirationDateTime = null;
    	/**
    	 * If timeToLive optional entry exists compare it against current time orElse compare global paramater
    	 */
    	if(timeToLive.isPresent()) {
    		System.out.println("TTL =>" +timeToLive.get());
    		expirationDateTime = this.cacheMap.get(key).getCreatedAt().plus(timeToLive.get(), ChronoUnit.MILLIS);
    	} 
        
        return expirationDateTime != null ?  LocalDateTime.now().isAfter(expirationDateTime) : false;
    }

    @Override
    public Optional<V> get(K key) {
    	/**
    	 * Remove the expired cache entries from the map so that only active entries are being processed.
    	 */
        this.clean();
        return Optional.ofNullable(this.cacheMap.get(key)).map(CacheValue::getValue);
    }

    @Override
    public void addEntry(K key, V value, Optional<Long> timeToLive) throws CacheOverFlowException {
    	Long ttl = null;
    	if(timeToLive.isPresent()) {
    		ttl = timeToLive.get();    		
    	} 
    	if(this.cacheMap.size() > this.maxEntries) {
    		throw new CacheOverFlowException("Cache Overflow!!");
    	}
    	this.cacheMap.put(key, this.createCacheValue(value, ttl));
    }

    protected CacheValue<V> createCacheValue(V value, Long timeToLive) {
    	// Obtain the current time
        LocalDateTime now = LocalDateTime.now();
        return new CacheValue<V>() {
            @Override
            public V getValue() {
                return value;
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return now;
            }

			@Override
			public Long getTimeToLive() {
				return timeToLive;
			}
        };
    }

    @Override
    public void remove(K key) {
        this.cacheMap.remove(key);
    }
    /**
     * Interface that keeps track of the cache value(s) and its associated creation time-stamp.
     * 
     * This interface only stores the value of the “cached” item (which is of the generic type V) 
     * and its creation date/time (java.time.LocalDateTime). 
     * 
     * @author raguramm
     *
     * @param <V>
     */
    protected interface CacheValue<V> {
        V getValue();

        LocalDateTime getCreatedAt();
        
        Long getTimeToLive();
    }
    
    public void clean() {
        for(K key: this.getExpiredKeys()) {
            this.remove(key);
        }
    }
    

    public void clear() {
        this.cacheMap = new HashMap<>();
    }

}
