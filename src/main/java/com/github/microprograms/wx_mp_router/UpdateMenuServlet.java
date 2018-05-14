package com.github.microprograms.wx_mp_router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.microprograms.wx_mp_router.utils.Fn;

import me.chanjar.weixin.common.api.WxConsts.MenuButtonType;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.exception.WxErrorException;

@WebServlet("/updateMenu")
public class UpdateMenuServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(UpdateMenuServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("updateMenu");
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("message", "成功");
        String wxMenuButtonsString = request.getParameter("wxMenuButtons");
        try {
            JSONArray wxMenuButtons = JSON.parseArray(wxMenuButtonsString);
            updateWxMenu(wxMenuButtons);
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

    private static void updateWxMenu(JSONArray wxMenuButtons) throws WxErrorException {
        WxMenu wxMenu = new WxMenu();
        List<WxMenuButton> buttons = new ArrayList<>();
        for (int i = 0; i < wxMenuButtons.size(); i++) {
            JSONObject wxMenuButton = wxMenuButtons.getJSONObject(i);
            if (StringUtils.isBlank(wxMenuButton.getString("parentId"))) {
                WxMenuButton button = new WxMenuButton();
                button.setName(wxMenuButton.getString("name"));
                List<WxMenuButton> subButtons = buildSubButtons(wxMenuButton.getString("id"), wxMenuButtons);
                boolean hasSubButtons = !subButtons.isEmpty();
                if (hasSubButtons) {
                    button.setSubButtons(subButtons);
                }
                String url = wxMenuButton.getString("url");
                boolean hasUrl = StringUtils.isNotBlank(url);
                if (!hasSubButtons && hasUrl) {
                    button.setType(MenuButtonType.VIEW);
                    button.setUrl(url);
                }
                buttons.add(button);
            }
        }
        wxMenu.setButtons(buttons);
        Fn.getWxMpService().getMenuService().menuCreate(wxMenu);
    }

    private static List<WxMenuButton> buildSubButtons(String parentId, JSONArray wxMenuButtons) {
        List<WxMenuButton> subButtons = new ArrayList<>();
        for (int i = 0; i < wxMenuButtons.size(); i++) {
            JSONObject wxMenuButton = wxMenuButtons.getJSONObject(i);
            if (parentId.equals(wxMenuButton.getString("parentId"))) {
                WxMenuButton subButton = new WxMenuButton();
                subButton.setName(wxMenuButton.getString("name"));
                subButton.setType(MenuButtonType.VIEW);
                subButton.setUrl(wxMenuButton.getString("url"));
                subButtons.add(subButton);
            }
        }
        return subButtons;
    }
}