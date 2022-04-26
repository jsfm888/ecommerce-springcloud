package com.njust.ecommerce.service.impl;

import com.njust.ecommerce.account.AddressInfo;
import com.njust.ecommerce.common.TableId;
import com.njust.ecommerce.dao.EcommerceAddressDao;
import com.njust.ecommerce.entity.EcommerceAddress;
import com.njust.ecommerce.filter.AccessContext;
import com.njust.ecommerce.service.IAddressService;
import com.njust.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AddressServiceImpl implements IAddressService {

    private final EcommerceAddressDao addressDao;

    public AddressServiceImpl(EcommerceAddressDao ecommerceAddressDao) {
        this.addressDao = ecommerceAddressDao;
    }

    @Override
    public TableId createAddressInfo(AddressInfo addressInfo) {
        //获取当前登录用户
        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();

        List<EcommerceAddress> ecommerceAddresses = addressInfo.getAddressItems().stream()
                .map(a -> EcommerceAddress.to(loginUserInfo.getId(), a))
                .collect(Collectors.toList());
        //保存到数据表并把返回记录的 id 给调用方
        List<EcommerceAddress> savedRecords = addressDao.saveAll(ecommerceAddresses);
        List<Long> ids = savedRecords.stream()
                                .map(EcommerceAddress::getId)
                                .collect(Collectors.toList());

        return new TableId(
                ids.stream().map(TableId.Id::new).collect(Collectors.toList())
        );
    }

    @Override
    public AddressInfo getCurrentAddressInfo() {
        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();
        List<EcommerceAddress> ecommerceAddresses = addressDao.findAllByUserId(loginUserInfo.getId());

        List<AddressInfo.AddressItem> addressItems = ecommerceAddresses.stream()
                                                .map(a -> a.toAddressItem())
                                                .collect(Collectors.toList());
        return new AddressInfo(loginUserInfo.getId(), addressItems);
    }

    @Override
    public AddressInfo getAddressInfoById(Long id) {
        EcommerceAddress ecommerceAddress = addressDao.findById(id).orElse(null);
        if(null == ecommerceAddress) {
            throw new RuntimeException("address is not exist");
        }
        AddressInfo.AddressItem addressItem = ecommerceAddress.toAddressItem();

        return new AddressInfo(
                ecommerceAddress.getUserId(),
                //包装成当个对象列表的api
                Collections.singletonList(addressItem)
        );
    }

    @Override
    public AddressInfo getAddressInfoByTableId(TableId tableId) {
        List<Long> ids = tableId.getIds().stream()
                            .map(TableId.Id::getId)
                            .collect(Collectors.toList());
        //ids 来自同一个 userId
        List<EcommerceAddress> ecommerceAddresses = addressDao.findAllById(ids);
        if(CollectionUtils.isEmpty(ecommerceAddresses)) {
            return new AddressInfo(-1L, Collections.emptyList());
        }
        List<AddressInfo.AddressItem> addressItems = ecommerceAddresses.stream()
                                    .map(EcommerceAddress::toAddressItem)
                                    .collect(Collectors.toList());


        return new AddressInfo(
                ecommerceAddresses.get(0).getUserId(),
                addressItems
        );
    }
}
