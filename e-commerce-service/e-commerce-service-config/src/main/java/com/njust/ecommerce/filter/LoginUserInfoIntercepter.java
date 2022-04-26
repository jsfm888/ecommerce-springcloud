package com.njust.ecommerce.filter;

import com.njust.ecommerce.constant.CommonConstant;
import com.njust.ecommerce.util.TokenParseUtil;
import com.njust.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户身份统一拦截
 * */
@Component
@Slf4j
@SuppressWarnings("all")
public class LoginUserInfoIntercepter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //部分请求不需要带有身份信息
        if(checkWhiteListUrl(request.getRequestURI())) {
            return true;
        }


        //获取请求头里的token
        String token = request.getHeader(CommonConstant.JWT_USER_INFO_KEY);
        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.parseUserInfoFormToken(token);
        } catch (Exception ex) {
            log.error("parse login user info error: [{}]", ex.getMessage(), ex);
        }
        //按理说解析出来的loginUserInfo不可能为空  示意一下
        if(null == loginUserInfo) {
            throw new RuntimeException("can not parse current login user");
        }

        log.info("set login user info: [{}]", loginUserInfo);
        AccessContext.setLoginUserInfo(loginUserInfo);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 在请求完全结束后调用，一般用于清理资源等工作
     * */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(null != AccessContext.getLoginUserInfo()) {
            AccessContext.clearLoginUserInfo();
        }
    }

    /**
     * 校验是否是白名单接口
     * */
    private boolean checkWhiteListUrl(String url) {
        return StringUtils.containsAny(
                url,
                "springfox", "v2", "swagger", "doc.html", "webjars"
        );
    }
}
