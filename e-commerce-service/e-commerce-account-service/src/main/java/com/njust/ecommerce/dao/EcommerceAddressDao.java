package com.njust.ecommerce.dao;

import com.njust.ecommerce.entity.EcommerceAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EcommerceAddressDao extends JpaRepository<EcommerceAddress, Long> {

    /** 根据 userId 查询所有的用户地址信息 */
    List<EcommerceAddress> findAllByUserId(Long userId);
}
