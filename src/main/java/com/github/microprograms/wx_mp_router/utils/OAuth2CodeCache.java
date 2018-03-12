package com.github.microprograms.wx_mp_router.utils;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class OAuth2CodeCache {
    private final static Cache<String, String> cache = CacheBuilder.newBuilder()
            // 设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            // 设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
            .concurrencyLevel(5)
            // 设置cache中的数据在写入之后的存活时间为10秒
            .expireAfterWrite(1, TimeUnit.MINUTES)
            // 构建cache实例
            .build();

    public static String getIfPresent(String code) {
        return cache.getIfPresent(code);
    }

    public static void put(String code, String token) {
        cache.put(code, token);
    }
}
