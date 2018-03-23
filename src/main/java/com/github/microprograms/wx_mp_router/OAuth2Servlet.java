package com.github.microprograms.wx_mp_router;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.microprograms.wx_mp_router.utils.ApiUtils;
import com.github.microprograms.wx_mp_router.utils.Fn;
import com.github.microprograms.wx_mp_router.utils.OAuth2CodeCache;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

@WebServlet("/oauth2")
public class OAuth2Servlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Servlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("oauth2, url={}, queryString={}", request.getRequestURL(), request.getQueryString());
        try {
            String code = request.getParameter("code");
            String redirect_uri = request.getParameter("redirect_uri");
            log.info("oauth2, code={}, redirect_uri={}", code, redirect_uri);
            String token = OAuth2CodeCache.getIfPresent(code);
            if (StringUtils.isBlank(token)) {
                token = getToken(code);
                OAuth2CodeCache.put(code, token);
            }
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            String redirect_uri_with_token = String.format("%s?token=%s", redirect_uri, token);
            log.info("oauth2, redirect_uri_with_token={}", redirect_uri_with_token);
            response.setHeader("Location", redirect_uri_with_token);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private static String getToken(String code) throws WxErrorException {
        WxMpService wxMpService = Fn.getWxMpService();
        WxMpOAuth2AccessToken auth2AccessToken = wxMpService.oauth2getAccessToken(code);
        WxMpUser user = wxMpService.oauth2getUserInfo(auth2AccessToken, "zh_CN");
        return getToken(user.getOpenId(), user.getNickname(), user.getHeadImgUrl());
    }

    private static String getToken(String wxOpenId, String wxNickname, String wxAvatarImgUrl) {
        try {
            JSONObject param = new JSONObject();
            param.put("apiName", "car_carat_app_api.System_WxLogin_Api");
            param.put("wxOpenId", wxOpenId);
            param.put("wxNickname", wxNickname);
            param.put("wxAvatarImgUrl", wxAvatarImgUrl);
            String responseString = ApiUtils.post(Fn.getConfig().getString("loginApiUrl"), param);
            return JSON.parseObject(responseString).getJSONObject("data").getString("token");
        } catch (Exception e) {
            return "";
        }
    }
}