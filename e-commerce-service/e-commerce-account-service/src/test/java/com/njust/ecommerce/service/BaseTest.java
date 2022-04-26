package com.njust.ecommerce.service;

import com.njust.ecommerce.filter.AccessContext;
import com.njust.ecommerce.vo.LoginUserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class BaseTest {

    protected final LoginUserInfo loginUserInfo = new LoginUserInfo(
            10L, "jsfm"
    );

    @Before
    public void init() {
        AccessContext.setLoginUserInfo(loginUserInfo);
    }

    @After
    public void destory() {
        AccessContext.clearLoginUserInfo();
    }

}
