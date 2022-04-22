package com.njust.ecommerce.service;

import com.njust.ecommerce.vo.UsernameAndPassword;

public interface IJWTService {

    /**
     * 根据用户名和密码生成token, 使用默认超时时间
     * */
    public String generateToken(String username, String password) throws Exception;

    /**
     * 根据用户名和密码生成token, 使用指定的超时时间
     * */
    public String generateToken(String username, String password, int expire) throws Exception;

    /**
     * 注册用户并生成 token 返回
     * */
    public String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception;
}
