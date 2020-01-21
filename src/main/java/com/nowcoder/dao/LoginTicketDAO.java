package com.nowcoder.dao;

import com.nowcoder.model.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginTicketDAO {
    int addTicket(LoginTicket ticket);
    LoginTicket selectByTicket(String ticket);
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
