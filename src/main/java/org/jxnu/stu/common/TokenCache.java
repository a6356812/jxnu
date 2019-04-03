package org.jxnu.stu.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * This is a tokenCache,used to prevent user overreach
 */
public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    /**
     * create a localCache,it will using URL when grows more then maxiMumSize
     */
    private static LoadingCache<String, String> localCache
            = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //create a default string value when the localCache is not hit
                @Override
                public String load(String s) throws Exception {
                    return "null";//prevernt to throw NullException when useing StringUtil.equals
                }
            });

    public static void set(String key, String value) {
        localCache.put(key, value);
    }

    public static String get(String key) {
        try {
            String value = localCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
            return value;
        } catch (Exception e) {
            logger.error("localCache get error", e);
        }
        return null;
    }
}
