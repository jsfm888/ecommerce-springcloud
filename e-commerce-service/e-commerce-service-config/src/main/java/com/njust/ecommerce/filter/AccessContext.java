package com.njust.ecommerce.filter;


import com.njust.ecommerce.vo.LoginUserInfo;

/**
 * 使用 ThreadLocal 单独存储每一个线程携带的 loginUserInfo 信息
 * 要即使清理我们保存在ThreadLocal中的loginUserInfo
 * 1. 避免内存泄漏
 * 2. 保证线程在重用时，不会出现数据混乱
 * */
public class AccessContext {

    private static final ThreadLocal<LoginUserInfo> loginUserInfo = new ThreadLocal<>();

    public static LoginUserInfo getLoginUserInfo() {
        return loginUserInfo.get();
    }

    public static void setLoginUserInfo(LoginUserInfo _loginUserInfo) {
        loginUserInfo.set( _loginUserInfo);
    }

    public static void clearLoginUserInfo() {
        loginUserInfo.remove();
    }
}
