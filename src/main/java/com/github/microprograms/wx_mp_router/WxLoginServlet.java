package com.github.microprograms.wx_mp_router;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.github.microprograms.wx_mp_router.utils.Fn;
import com.typesafe.config.Config;

import me.chanjar.weixin.common.util.http.URIUtil;

@WebServlet("/wxLogin")
public class WxLoginServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(WxLoginServlet.class);
    private static final long serialVersionUID = 1L;
    private static final String CONNECT_OAUTH2_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&connect_redirect=1#wechat_redirect";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirect_uri = request.getParameter("redirect_uri");
        String scope = request.getParameter("scope");
        log.info("wxLogin -> redirect_uri={}, scope={}", redirect_uri, scope);
        try {
            JSONObject state = new JSONObject();
            state.put("redirect_uri", redirect_uri);
            Config config = Fn.getConfig();
            String wxOAuth2RedirectUri = config.getString("wxOAuth2RedirectUri");
            String appId = config.getString("appId");
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", oauth2buildAuthorizationUrl(appId, wxOAuth2RedirectUri, scope, URIUtil.encodeURIComponent(state.toJSONString())));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static String oauth2buildAuthorizationUrl(String appId, String redirectURI, String scope, String state) {
        return String.format(CONNECT_OAUTH2_AUTHORIZE_URL, appId, URIUtil.encodeURIComponent(redirectURI), scope, StringUtils.trimToEmpty(state));
    }
}