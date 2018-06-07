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
import com.github.microprograms.wx_mp_router.utils.AutoReplyUtils;

@WebServlet("/updateAutoReply")
public class UpdateAutoReplyServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(UpdateAutoReplyServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("updateAutoReply");
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("message", "成功");
        try {
            AutoReplyUtils.pull();
        } catch (Exception e) {
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