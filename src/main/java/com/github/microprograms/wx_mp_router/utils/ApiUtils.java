package com.github.microprograms.wx_mp_router.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class ApiUtils {
    public static final int httpConnectTimeoutMs = 8000;
    public static final int httpReadTimeoutMs = 10000;
    private static final Logger log = LoggerFactory.getLogger(ApiUtils.class);

    public static String post(String url, JSONObject param) throws IOException {
        String requestId = UUID.randomUUID().toString();
        log.debug("Post Request, url={}, param={}, requestId={}", url, param.toJSONString(), requestId);
        String reqBody = param.toJSONString();
        URL httpUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(httpConnectTimeoutMs);
        httpURLConnection.setReadTimeout(httpReadTimeoutMs);
        httpURLConnection.connect();
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(reqBody.getBytes("UTF-8"));
        // 获取内容
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line).append("\n");
        }
        String resp = stringBuffer.toString();
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
        log.debug("Post Response, resp={}, requestId={}", resp, requestId);
        return resp;
    }
}
