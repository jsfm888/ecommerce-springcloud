package com.njust.ecommerce.service;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.util.TokenParseUtil;
import com.njust.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class JWTServiceTest {

    @Autowired
    private IJWTService ijwtService;

    @Test
    public void testGenerateAndParseToken() throws Exception {
        String token = ijwtService.generateToken("jsfm", "25d55ad283aa400af464c76d713c07ad");

        log.info("jwt token is: [{}]", token);

        LoginUserInfo loginUserInfo = TokenParseUtil.parseUserInfoFormToken(token);

        log.info("parse token: [{}]", JSON.toJSONString(loginUserInfo));

    }

}
