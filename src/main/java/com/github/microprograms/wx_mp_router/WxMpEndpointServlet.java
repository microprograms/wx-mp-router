package com.github.microprograms.wx_mp_router;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.microprograms.wx_mp_router.utils.Fn;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

@WebServlet("/echo")
public class WxMpEndpointServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(WxMpEndpointServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("recv request");
        try {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);

            String signature = request.getParameter("signature");
            String nonce = request.getParameter("nonce");
            String timestamp = request.getParameter("timestamp");

            WxMpService wxMpService = Fn.getWxMpService();
            if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
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

            String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ? "raw" : request.getParameter("encrypt_type");

            if ("raw".equals(encryptType)) {
                // 明文传输的消息
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
                WxMpXmlOutMessage outMessage = Fn.getWxMpRouter().route(inMessage);
                response.getWriter().println(outMessage == null ? "" : outMessage.toXml());
                return;
            }

            if ("aes".equals(encryptType)) {
                // 是aes加密的消息
                String msgSignature = request.getParameter("msg_signature");
                WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxMpService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
                WxMpXmlOutMessage outMessage = Fn.getWxMpRouter().route(inMessage);
                response.getWriter().println(outMessage == null ? "" : outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage()));
                return;
            }

            response.getWriter().println("不可识别的加密类型");
            return;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }
}