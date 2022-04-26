package com.njust.ecommerce.service;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.account.AddressInfo;
import com.njust.ecommerce.common.TableId;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

/**
 * 用户地址相关服务接口测试
 * */
@Slf4j
public class AccountServiceTest extends BaseTest {

    @Autowired
    private IAddressService addressService;

    @Test
    public void testCreateAddressInfo() {
        AddressInfo.AddressItem addressItem = new AddressInfo.AddressItem();
        addressItem.setUsername("jsfm");
        addressItem.setPhone("15150566362");
        addressItem.setProvince("安徽省");
        addressItem.setCity("阜阳市");
        addressItem.setAddressDetail("临泉县诚信小学对面");

        log.info("test create address info: [{}]", JSON.toJSONString(
                addressService.createAddressInfo(new AddressInfo(
                        loginUserInfo.getId(), Collections.singletonList(addressItem)
                ))
        ));
    }

    @Test
    public void testGetCurrentAddressInfo() {
        log.info("test get current address info: [{}]", JSON.toJSONString(
                addressService.getCurrentAddressInfo()
        ));
    }

    @Test
    public void testGetAddressInfoById() {
        log.info("test get address info by id: [{}]", JSON.toJSONString(
                addressService.getAddressInfoById(10L)
        ));
    }

    @Test
    public void testGetAddressInfoByTableId(){
        log.info("test get address info by tableId: [{}]", JSON.toJSONString(
                addressService.getAddressInfoByTableId(new TableId(
                        Collections.singletonList(new TableId.Id(10L))))
        ));
    }
}
