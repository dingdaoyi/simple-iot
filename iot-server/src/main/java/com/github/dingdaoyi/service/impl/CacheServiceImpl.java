package com.github.dingdaoyi.service.impl;

import com.github.dingdaoyi.service.CacheService;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * @author dingyunwei
 */
@Service
@AllArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final CacheManager cacheManager;
    // 删除缓存
    @Override
    public void evictCache(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    // 删除整个缓存
    @Override
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear(); // 清空整个缓存
        }
    }
}