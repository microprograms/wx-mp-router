package com.github.microprograms.wx_mp_router;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceApacheHttpClientImpl;

@WebServlet("/echo")
public class WxMpEndpointServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(WxMpEndpointServlet.class);
    private static final long serialVersionUID = 1L;
    private WxMpService wxMpService;

    public WxMpEndpointServlet() {
        initWeixin();
    }

    private void initWeixin() {
        try (InputStream is = ClassLoader.getSystemResourceAsStream("config.xml")) {
            WxMpConfig config = WxMpConfig.fromXml(is);
            log.info("initWeixin -> {}", config.toString());
            wxMpService = new WxMpServiceApacheHttpClientImpl();
            wxMpService.setWxMpConfigStorage(config);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        if (!this.wxMpService.checkSignature(timestamp, nonce, signature)) {
            log.error("消息签名不正确，说明不是公众平台发过来的消息 -> signature={}, nonce={}, timestamp={}", signature, nonce, timestamp);
            response.getWriter().println("非法请求");
            return;
        }

        String echostr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echostr)) {
            log.info("说明是一个仅仅用来验证的请求，回显echostr -> signature={}, nonce={}, timestamp={}, echostr={}", signature, nonce, timestamp, echostr);
            response.getWriter().println(echostr);
            return;
        }

        log.error("不可识别的加密类型");
        response.getWriter().println("不可识别的加密类型");
    }
}