package com.github.microprograms.wx_mp_router.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;

public class AutoReplyUtils {
    private static final Logger log = LoggerFactory.getLogger(AutoReplyUtils.class);
    private static List<JSONObject> autoReplies = new ArrayList<>();

    static {
        try {
            pull();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 类型(0:关键词回复,1:收到消息回复,2:被关注回复)
     */
    public static List<JSONObject> getAutoReply(int type) {
        List<JSONObject> list = new ArrayList<>();
        for (JSONObject x : autoReplies) {
            if (x.getIntValue("type") == type) {
                list.add(x);
            }
        }
        return list;
    }

    public static JSONObject getSubscribeAutoReply() {
        List<JSONObject> list = getAutoReply(2);
        return list.isEmpty() ? null : list.get(0);
    }

    public static List<JSONObject> getKeywordAutoReply() {
        return getAutoReply(0);
    }

    public static JSONObject getRecvMessageAutoReply() {
        List<JSONObject> list = getAutoReply(1);
        return list.isEmpty() ? null : list.get(0);
    }

    public synchronized static void pull() throws IOException {
        JSONObject param = new JSONObject();
        param.put("apiName", "car_carat_manager_api.WxAutoReply_QueryAll_Api");
        String rawResponse = ApiUtils.post(Fn.getConfig().getString("queryAllWxAutoReplyConfigsApiUrl"), param);
        JSONObject response = JSON.parseObject(rawResponse);
        if (response.getIntValue("code") != 0) {
            throw new RuntimeException("pull error, message=" + response.getString("message"));
        }
        List<JSONObject> autoReplies = new ArrayList<>();
        JSONArray data = response.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            autoReplies.add(data.getJSONObject(i));
        }
        AutoReplyUtils.autoReplies = autoReplies;
    }

    public static WxMpXmlOutMessage buildWxMpXmlOutMessage(JSONObject config, String fromUser, String toUser) {
        if (config == null) {
            return null;
        }
        if (config.getIntValue("contentType") == 1) {
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
            item.setTitle(config.getString("title"));
            item.setDescription(config.getString("description"));
            item.setPicUrl(config.getString("picUrl"));
            item.setUrl(config.getString("url"));
            return WxMpXmlOutMessage.NEWS().addArticle(item).fromUser(fromUser).toUser(toUser).build();
        } else {
            return WxMpXmlOutMessage.TEXT().content(config.getString("text")).fromUser(fromUser).toUser(toUser).build();
        }
    }
}
