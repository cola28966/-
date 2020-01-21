package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;

import com.nowcoder.utils.WendaUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public User findUserByName(String name){
        return userDAO.selectByName(name);
    }

    public Map<String,Object>  login(String username,String password){
        Map<String,Object>  vo = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            vo.put("msg", "用户名不能为空");
            return vo;
        }

        if (StringUtils.isBlank(password)) {
            vo.put("msg", "密码不能为空");
            return vo;
        }
        User user = findUserByName(username);
        if(user==null){
            vo.put("msg","没有该用户");
        }
        else if(!user.getPassword().equals(WendaUtils.md5(password+user.getSalt()))){
            vo.put("msg","密码错误");
        }
        else {
            String ticket = addLoginTicket(user.getId());
            vo.put("ticket", ticket);
            vo.put("user",user);
        }
        return vo;
    }
    public Map<String,Object> register(String username,String password){
        Map<String,Object> vo = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            vo.put("msg", "用户名不能为空");
            return vo;
        }

        if (StringUtils.isBlank(password)) {
            vo.put("msg", "密码不能为空");
            return vo;
        }
        User user = findUserByName(username);
        if(user!=null){
            vo.put("msg","姓名已经存在");
            return vo;
        }
        user = new User();
        user.setName(username);
        user.setSalt(WendaUtils.generateUUID().substring(0,5));
        user.setPassword(WendaUtils.md5(password+user.getSalt()));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        userDAO.addUser(user);
        String ticket = addLoginTicket(user.getId());
        vo.put("ticket",ticket);
        return vo;
    }
    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(WendaUtils.generateUUID());
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }
    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }
}
