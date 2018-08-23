package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * guava本地存储token的管理类
 */
public class TokenCache {
    public static final String TOKEN_PRIFIX = "token_";
    // 声明日志信息
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    // 声明guava中的本地静态内存块,构建本地cache,initialCapacity是设置缓存的初始化容量，maximumSize设置缓存的最大容量
    // 当超过这个容量guawa的cache会使用LRU算法来移除缓存项，expireAfterAccess设置有效期,build选cacheloader,这是一个抽象类，我们要在里面写匿名实现
    private static LoadingCache<String, String > localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                // 此方法是默认的的数据加载实现，当get取值的时候，key没有相应的取值，就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    // 这个方法是当我么你的key没有命中的时候会调用的方法
                    // 为了防止equals空指针问题，这里不建议直接返回null，建议返回字符串的null
                    return "null";
                }


            });
    // 创建一个setKey的方法
    public static void setKey(String key, String value){
        localCache.put(key, value);
    }

    // getKey
    public static String  getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            // 打印异常到log
            logger.error("localCache get error", e);
        }
        return null;
    }
}
