package com.github.microprograms.wx_mp_router;

import java.io.File;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.microprograms.wx_mp_router.utils.Fn;
import com.github.microprograms.wx_mp_router.utils.HttpClientUtils;

@WebServlet("/downloadWxPicture")
public class DownloadWxPictureServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DownloadWxPictureServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String mediaId = request.getParameter("mediaId");
            File file = Fn.getWxMpService().getMaterialService().mediaDownload(mediaId);
            log.info("downloadWxPicture, mediaId={}, file={}", mediaId, file.getPath());
            String uploadFileApiUrl = Fn.getConfig().getString("uploadFileApiUrl");
            String resp = HttpClientUtils.getInstance().uploadFile(uploadFileApiUrl, file.getPath(), "file", null);
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(resp);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}