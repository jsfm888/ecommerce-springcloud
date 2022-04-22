package com.njust.ecommerce.util;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.constant.CommonConstant;
import com.njust.ecommerce.vo.LoginUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

/**
 * Jwt Token 解析工具类
 * */
public class TokenParseUtil {

    /**
     * 从token中解析登录用户信息
     * */
    public static LoginUserInfo parseUserInfoFormToken(String token) throws Exception {
        if(null == token) {
            return null;
        }

        Jws<Claims> claimsJws = parseToken(token, getPublic());
        Claims claim = claimsJws.getBody();

        //如果 token 已经过期
        if(claim.getExpiration().before(Calendar.getInstance().getTime())) {
            return null;
        }
        //返回 token 中的用户信息
        return JSON.parseObject(claim.get(CommonConstant.JWT_USER_INFO_KEY).toString(),
                                LoginUserInfo.class);
    }

    /**
     * 根据公钥去解析token
     * */
    public static Jws<Claims> parseToken(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }


    /**
     * 根据本地存储的公钥获取到 PublicKey 对象
     * */
    public static PublicKey getPublic() throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(CommonConstant.PUBLIC_KEY)
        );
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
}
