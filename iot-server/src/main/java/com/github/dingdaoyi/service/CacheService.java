package com.github.dingdaoyi.service;

public interface CacheService {

      String TSL_MODEL_CACHE = "tslModelCache";

    /**
     * 删除缓存
     * @param cacheName
     * @param key
     */
    void evictCache(String cacheName, String key);

    /**
     * 清理整个缓存
     * @param cacheName
     */
    void clearCache(String cacheName);

    /**
     * 清理物模型缓存
     * @param productKey 产品key
     */
   default void evictTslModel(String productKey){
       evictCache(TSL_MODEL_CACHE, productKey);
   }
}
