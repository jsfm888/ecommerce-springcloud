package com.njust.ecommerce.service;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.common.TableId;
import com.njust.ecommerce.goods.DeductGoodsInventory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsServiceTest {

    @Autowired
    private IGoodsService goodsService;

    @Test
    public void testGetGoodsInfoByTableId() {
        List<Long> ids = Arrays.asList(10L, 11L, 12L);
        List<TableId.Id> tIds = ids.stream().map(TableId.Id::new).collect(Collectors.toList());
        log.info("test get goods info by table ids: [{}]",
                JSON.toJSONString(goodsService.getGoodsInfoByTableId(new TableId(tIds))
        ));
    }

    @Test
    public void testGetSimpleGoodsInfoByPage() {
         log.info("test get simple goods info by page: [{}]", JSON.toJSONString(
                 goodsService.getSimpleGoodsInfoByPage(-24)
         ));
    }

    @Test
    public void testGetSimpleGoodsInfoByTableId() {
        List<Long> ids = Arrays.asList(10L, 11L);
        List<TableId.Id> tIds = ids.stream().map(TableId.Id::new).collect(Collectors.toList());
        log.info("test get simple goods info by tableId: [{}]", JSON.toJSONString(
                goodsService.getSimpleGoodsInfoByTableId(new TableId(tIds))
        ));
    }

    @Test
    public void testDeductGoodsInventory() {
        List<DeductGoodsInventory> deductGoodsInventories = Arrays.asList(
                new DeductGoodsInventory(11L, 1000),
                new DeductGoodsInventory(12L, 5000)
        );
        log.info("test deduct goods inventory: [{}]", JSON.toJSONString(
                goodsService.deductGoodsInventory(deductGoodsInventories)
        ));
    }
}
