package com.njust.ecommerce.service;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA 非对称加密算法: 生成公钥和私钥
 * */

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RSATest {

    @Test
    public void generateKeyBytes() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        log.info("private key: [{}]", Base64.encode(privateKey.getEncoded()));
        log.info("public key: [{}]", Base64.encode(publicKey.getEncoded()));
    }
}
