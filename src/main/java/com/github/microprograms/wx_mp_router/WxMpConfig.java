package com.github.microprograms.wx_mp_router;

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;

@XStreamAlias("xml")
public class WxMpConfig extends WxMpInMemoryConfigStorage {

    public static WxMpConfig fromXml(InputStream is) {
        XStream xstream = XStreamInitializer.getInstance();
        xstream.processAnnotations(WxMpConfig.class);
        return (WxMpConfig) xstream.fromXML(is);
    }

    @Override
    public String toString() {
        return "SimpleWxConfigProvider [appId=" + this.appId + ", secret=" + this.secret + ", accessToken=" + this.accessToken + ", expiresTime=" + this.expiresTime + ", token=" + this.token + ", aesKey=" + this.aesKey + "]";
    }
}