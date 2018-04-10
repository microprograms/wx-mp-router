package com.github.microprograms.wx_mp_router;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
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
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("message", "成功");
        try {
            WxMpQrcodeService qrcodeService = Fn.getWxMpService().getQrcodeService();
            WxMpQrCodeTicket qrCodeTicket = qrcodeService.qrCodeCreateTmpTicket(scene_str, 60 * 5);
            String qrCodePictureUrl = qrcodeService.qrCodePictureUrl(qrCodeTicket.getTicket());
            resp.put("data", qrCodePictureUrl);
        } catch (WxErrorException e) {
            log.error("", e);
            resp.put("code", 1);
            resp.put("message", "失败");
            resp.put("stackTrace", ExceptionUtils.getStackTrace(e));
        } finally {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(resp.toJSONString());
        }
    }
}