package com.njust.ecommerce.controller;


import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.annotation.IgnoreResponseAdvice;
import com.njust.ecommerce.service.IJWTService;
import com.njust.ecommerce.vo.JwtToken;
import com.njust.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/authority")
public class AuthorityController {

    private final IJWTService ijwtService;

    public AuthorityController(IJWTService ijwtService) {
        this.ijwtService = ijwtService;
    }

    /** 用户名密码登录返回 token , 且返回信息中没有统一响应的包装 */
    @IgnoreResponseAdvice
    @PostMapping("/token")
    public JwtToken token(@RequestBody UsernameAndPassword usernameAndPassword) throws Exception {
        log.info("request to get token with param: [{}]",
                JSON.toJSONString(usernameAndPassword));

        return new JwtToken(ijwtService.generateToken(
                usernameAndPassword.getUsername(),usernameAndPassword.getPassword()
        ));
    }

    /** 用户名密码注册用户并返回 token  */
    @IgnoreResponseAdvice
    @PostMapping("/register")
    public JwtToken register(@RequestBody UsernameAndPassword usernameAndPassword) throws Exception {
        log.info("register user with param: [{}]",
                JSON.toJSONString(usernameAndPassword));
        return new JwtToken(ijwtService.registerUserAndGenerateToken(usernameAndPassword));
    }
}
