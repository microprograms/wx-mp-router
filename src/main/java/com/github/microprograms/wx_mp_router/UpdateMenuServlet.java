package com.github.microprograms.wx_mp_router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.microprograms.wx_mp_router.utils.Fn;

import me.chanjar.weixin.common.api.WxConsts.MenuButtonType;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;

@WebServlet("/updateMenu")
public class UpdateMenuServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(UpdateMenuServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("updateMenu");
        try {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            WxMenu wxMenu = new WxMenu();
            List<WxMenuButton> buttons = new ArrayList<>();
            WxMenuButton loginButton = new WxMenuButton();
            loginButton.setName("登录");
            loginButton.setType(MenuButtonType.VIEW);
            loginButton.setUrl("http://47.104.17.187/wx/login.html");
            buttons.add(loginButton);
            wxMenu.setButtons(buttons);
            Fn.getWxMpService().getMenuService().menuCreate(wxMenu);
            response.getWriter().println("更新成功");
        } catch (Exception e) {
            log.error("", e);
        }
    }
}