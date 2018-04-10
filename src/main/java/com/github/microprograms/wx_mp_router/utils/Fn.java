package com.github.microprograms.wx_mp_router.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceApacheHttpClientImpl;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public class Fn {
    private static final Logger log = LoggerFactory.getLogger(Fn.class);
    private static Config config = ConfigFactory.load();
    private static WxMpService wxMpService;
    private static WxMpMessageRouter wxMpRouter;

    static {
        WxMpInMemoryConfigStorage storage = new WxMpInMemoryConfigStorage();
        storage.setAppId(config.getString("wxAppId"));
        storage.setSecret(config.getString("wxSecret"));
        storage.setToken(config.getString("wxToken"));
        storage.setAesKey(config.getString("wxAesKey"));
        log.info("initWeixin -> {}", storage.toString());
        wxMpService = new WxMpServiceApacheHttpClientImpl();
        wxMpService.setWxMpConfigStorage(storage);
        wxMpRouter = new WxMpMessageRouter(wxMpService);
        wxMpRouter
                // 关注
                .rule().async(false).event(WxConsts.EventType.SUBSCRIBE).handler(new WxMpMessageHandler() {
                    @Override
                    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
                        String eventKey = wxMessage.getEventKey(); // eventKey=qrscene_example
                        if (eventKey != null && eventKey.startsWith("qrscene_")) {
                            String qrscene = eventKey.substring("qrscene_".length());
                        }
                        return null;
                    }
                }).end();
    }

    public static Config getConfig() {
        return config;
    }

    public static WxMpService getWxMpService() {
        return wxMpService;
    }

    public static WxMpMessageRouter getWxMpRouter() {
        return wxMpRouter;
    }

    public static WxMpMessageRouter getQrscene() {
        return wxMpRouter;
    }
}
