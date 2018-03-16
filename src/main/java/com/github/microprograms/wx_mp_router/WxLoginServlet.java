package com.github.microprograms.wx_mp_router;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            Config config = Fn.getConfig();
            String wxOAuth2RedirectUri = String.format("%s?redirect_uri=%s", config.getString("wxOAuth2RedirectUri"), redirect_uri);
            String wxAppId = config.getString("wxAppId");
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            String oauth2AuthorizationUrl = oauth2buildAuthorizationUrl(wxAppId, wxOAuth2RedirectUri, scope);
            log.info("wxLogin -> oauth2AuthorizationUrl={}", oauth2AuthorizationUrl);
            response.setHeader("Location", oauth2AuthorizationUrl);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static String oauth2buildAuthorizationUrl(String appId, String redirectURI, String scope) {
        return String.format(CONNECT_OAUTH2_AUTHORIZE_URL, appId, URIUtil.encodeURIComponent(redirectURI), scope, "state");
    }
}