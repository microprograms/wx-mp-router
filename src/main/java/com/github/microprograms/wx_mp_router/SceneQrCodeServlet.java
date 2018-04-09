package com.github.microprograms.wx_mp_router;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.microprograms.wx_mp_router.utils.Fn;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

@WebServlet("/sceneQrCode")
public class SceneQrCodeServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(SceneQrCodeServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String scene_str = request.getParameter("scene_str");
        try {
            WxMpQrcodeService qrcodeService = Fn.getWxMpService().getQrcodeService();
            WxMpQrCodeTicket qrCodeTicket = qrcodeService.qrCodeCreateTmpTicket(scene_str, 60 * 5);
            String qrCodePictureUrl = qrcodeService.qrCodePictureUrl(qrCodeTicket.getTicket());
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            log.info("sceneQrCode -> qrCodePictureUrl={}", qrCodePictureUrl);
            response.setHeader("Location", qrCodePictureUrl);
        } catch (WxErrorException e) {
            log.error("", e);
        }
    }
}