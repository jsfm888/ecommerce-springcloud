package com.njust.ecommerce.dao;

import com.njust.ecommerce.entity.EcommerceBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcommerceBalanceDao extends JpaRepository<EcommerceBalance, Long> {

    EcommerceBalance findByUserId(Long userId);

}

