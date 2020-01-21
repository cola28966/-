package com.nowcoder.controller;

import com.nowcoder.model.ViewObject;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model, @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    public String loginPage(Model model, @RequestParam("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam(value="next", defaultValue = "false") String next,
                            @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                            HttpServletResponse response) {
        try {
            Map<String,Object> vo;
            vo = userService.login(username, password);
            System.out.println(vo);
            if (!vo.containsKey("ticket") ) {
               model.addAttribute("msg",vo.get("msg"));
               return "login";
            } else {
                Cookie cookie = new Cookie("ticket", vo.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                model.addAttribute("user",vo.get("user"));
                if (StringUtils.isNotBlank(next)&&!next.equals("false")) {
                    return "redirect:" + next;
                }
                return "index";
            }
        }catch (Exception e){
            logger.error("登录异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
    }

    @RequestMapping(path = {"/register"}, method = {RequestMethod.POST})
    public String register(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                           @RequestParam(value="next", defaultValue = "false") String next,
                           HttpServletResponse response) {
        try {
            Map<String,Object> vo;
            vo = userService.register(username, password);
            System.out.println(vo);
            if (vo.containsKey("ticket") ) {
                Cookie cookie = new Cookie("ticket", vo.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "index";
            } else {
                model.addAttribute("msg", vo.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }

    }
    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        hostHolder.clear();
        return "redirect:/";
    }
}
