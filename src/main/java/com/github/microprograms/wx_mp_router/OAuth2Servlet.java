package com.github.microprograms.wx_mp_router;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.microprograms.wx_mp_router.utils.ApiUtils;
import com.github.microprograms.wx_mp_router.utils.Fn;
import com.github.microprograms.wx_mp_router.utils.OAuth2CodeCache;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

@WebServlet("/oauth2")
public class OAuth2Servlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Servlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("oauth2");
        try {
            String code = request.getParameter("code");
            if (OAuth2CodeCache.exist(code)) {
                return;
            }
            OAuth2CodeCache.put(code);
            log.info("oauth2 -> code={}", code);
            WxMpService wxMpService = Fn.getWxMpService();
            WxMpOAuth2AccessToken auth2AccessToken = wxMpService.oauth2getAccessToken(code);
            log.info("oauth2 -> auth2AccessToken={}", auth2AccessToken.toString());
            WxMpUser user = wxMpService.oauth2getUserInfo(auth2AccessToken, "zh_CN");
            log.info("oauth2 -> user={}", user.toString());
            JSONObject state = JSON.parseObject(request.getParameter("state"));
            log.info("oauth2 -> state={}", state.toJSONString());
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", String.format("%s?token=%s", state.getString("redirect_uri"), getToken(user.getOpenId(), user.getNickname(), user.getHeadImgUrl())));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private static String getToken(String wxOpenId, String wxNicknme, String wxAvatarImgUrl) {
        try {
            JSONObject param = new JSONObject();
            param.put("apiName", "car_carat_app_api.System_WxLogin_Api");
            param.put("wxOpenId", wxOpenId);
            param.put("wxNicknme", wxNicknme);
            param.put("wxAvatarImgUrl", wxAvatarImgUrl);
            String responseString = ApiUtils.post(Fn.getConfig().getString("loginApiUrl"), param);
            return JSON.parseObject(responseString).getJSONObject("data").getString("token");
        } catch (Exception e) {
            return "";
        }
    }
}