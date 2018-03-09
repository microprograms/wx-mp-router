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

    public static boolean exist(String code) {
        return cache.getIfPresent(code) != null;
    }

    public static void put(String code) {
        cache.put(code, code);
    }

    public static void main(String[] args) throws Exception {
        String code = "081YJFkQ1UPat81Kl0lQ1IA2lQ1YJFkO";
        System.out.println(exist(code));
        put(code);
        for (int i = 0; i < 10; i++) {
            System.out.println(exist(code));
            Thread.sleep(10 * 1000);
        }
    }
}
