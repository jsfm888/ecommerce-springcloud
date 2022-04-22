package com.njust.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.constant.AuthorityConstant;
import com.njust.ecommerce.constant.CommonConstant;
import com.njust.ecommerce.dao.EcommerceUserDao;
import com.njust.ecommerce.entity.EcommerceUser;
import com.njust.ecommerce.service.IJWTService;
import com.njust.ecommerce.vo.LoginUserInfo;
import com.njust.ecommerce.vo.UsernameAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class JWTServiceImpl implements IJWTService {

    private final EcommerceUserDao ecommerceUserDao;

    public JWTServiceImpl(EcommerceUserDao ecommerceUserDao) {
        this.ecommerceUserDao = ecommerceUserDao;
    }

    @Override
    public String generateToken(String username, String password) throws Exception {
        return generateToken(username, password, 0);
    }

    @Override
    public String generateToken(String username, String password, int expire) throws Exception {
        //生成token之前先验证用户名和密码是否正确
        EcommerceUser ecommerceUser = ecommerceUserDao.findByUsernameAndPassword(username, password);
        if(null == ecommerceUser) {
            log.info("can not find user: [{}], [{}]", username, password);
            return null;
        }

        LoginUserInfo loginUserInfo = new LoginUserInfo(ecommerceUser.getId(), ecommerceUser.getUsername());

        //超时时间若小于等于0，使用默认超时时间
        if(expire <= 0) {
            expire = AuthorityConstant.DEFAULT_EXPIRE_DAT;
        }

        //计算超时时间
        ZonedDateTime zonedDateTime = LocalDate.now().plus(expire, ChronoUnit.DAYS)
                                        .atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zonedDateTime.toInstant());

        return Jwts.builder()
                //jwt payload KV
                .claim(CommonConstant.JWT_USER_INFO_KEY, JSON.toJSONString(loginUserInfo))
                //jwt id
                .setId(UUID.randomUUID().toString())
                //jwt 过期时间
                .setExpiration(expireDate)
                //jwt 签名算法
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception {
        EcommerceUser oldUser = ecommerceUserDao.findByUsername(usernameAndPassword.getUsername());
        if(null != oldUser) { //用户已存在
            log.error("user is registered: [{}]", oldUser.getUsername());
            return null;
        }

        EcommerceUser ecommerceUser = new EcommerceUser();
        ecommerceUser.setUsername(usernameAndPassword.getUsername());
        ecommerceUser.setPassword(usernameAndPassword.getPassword());
        ecommerceUser.setExtraInfo("{}");
        //保存注册用户记录到数据库
        ecommerceUser = ecommerceUserDao.save(ecommerceUser);

        log.info("register user success: [{}], [{}]",
                ecommerceUser.getUsername(), ecommerceUser.getId());

        return generateToken(ecommerceUser.getUsername(), ecommerceUser.getPassword());
    }

    /**
     * 根据本地存储的私钥获取到 PrivateKey 对象
     * */
    private PrivateKey getPrivateKey() throws Exception {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthorityConstant.PRIVATE_KEY)
        );
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(priPKCS8);
    }
}
