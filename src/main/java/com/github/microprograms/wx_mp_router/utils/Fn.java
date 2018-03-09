package com.github.microprograms.wx_mp_router.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceApacheHttpClientImpl;

public class Fn {
    private static final Logger log = LoggerFactory.getLogger(Fn.class);
    private static WxMpService wxMpService;
    private static Config config = ConfigFactory.load();

    static {
        WxMpInMemoryConfigStorage storage = new WxMpInMemoryConfigStorage();
        storage.setAppId(config.getString("wxAppId"));
        storage.setSecret(config.getString("wxSecret"));
        storage.setToken(config.getString("wxToken"));
        storage.setAesKey(config.getString("wxAesKey"));
        log.info("initWeixin -> {}", storage.toString());
        wxMpService = new WxMpServiceApacheHttpClientImpl();
        wxMpService.setWxMpConfigStorage(storage);
    }

    public static Config getConfig() {
        return config;
    }

    public static WxMpService getWxMpService() {
        return wxMpService;
    }
}
