package com.njust.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.common.TableId;
import com.njust.ecommerce.constant.GoodsConstant;
import com.njust.ecommerce.dao.EcommerceGoodsDao;
import com.njust.ecommerce.entity.EcommerceGoods;
import com.njust.ecommerce.goods.DeductGoodsInventory;
import com.njust.ecommerce.goods.GoodsInfo;
import com.njust.ecommerce.goods.SimpleGoodsInfo;
import com.njust.ecommerce.service.IGoodsService;
import com.njust.ecommerce.vo.PagedSimpleGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl implements IGoodsService {

    private final EcommerceGoodsDao goodsDao;
    private final StringRedisTemplate redisTemplate;

    public GoodsServiceImpl(StringRedisTemplate redisTemplate,
                            EcommerceGoodsDao goodsDao) {
        this.redisTemplate = redisTemplate;
        this.goodsDao = goodsDao;
    }

    @Override
    public List<GoodsInfo> getGoodsInfoByTableId(TableId tableId) {
        //详细地商品信息不能从redis中去获取
        List<Long> ids = tableId.getIds().stream()
                                    .map(TableId.Id::getId)
                                    .collect(Collectors.toList());
        log.info("get goods info by ids: [{}]", JSON.toJSONString(ids));
        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(
                goodsDao.findAllById(ids)
        );
        return ecommerceGoods.stream()
                .map(EcommerceGoods::toGoodsInfo)
                .collect(Collectors.toList());
    }

    @Override
    public PagedSimpleGoodsInfo getSimpleGoodsInfoByPage(int page) {
        if(page < 1) page = 1;

        Pageable pageable = PageRequest.of(
                page - 1, 10, Sort.by("id").descending()
        );

        Page<EcommerceGoods> orderPage = goodsDao.findAll(pageable);
        //是否还有更多页
        boolean hasMore = orderPage.getTotalPages() > page;
        return new PagedSimpleGoodsInfo(
                orderPage.getContent().stream()
                        .map(EcommerceGoods::toSimple).collect(Collectors.toList()),
                hasMore
        );
    }

    @Override
    public List<SimpleGoodsInfo> getSimpleGoodsInfoByTableId(TableId tableId) {
        //根据 tableId 获取简单的商品信息，先从 redis 中获取，获取不到的再去db中获取
        List<Object> goodIds =  tableId.getIds().stream()
                .map(id -> id.getId().toString()).collect(Collectors.toList());
        List<Object> cachedSimpleGoodsInfo = redisTemplate.opsForHash().multiGet(
                GoodsConstant.ECOMMERCE_GOODS_DICT_KEY, goodIds
        );

        //如果从redis中查到了数据
        if(CollectionUtils.isNotEmpty(cachedSimpleGoodsInfo)) {
            if(cachedSimpleGoodsInfo.size() == goodIds.size()) { //全部都能从redis中查询
                log.info("get simple goods info by ids (from cache): [{}]",
                        JSON.toJSONString(goodIds));
                return parseCachedGoodsInfo(cachedSimpleGoodsInfo);
            } else { //部分从redis中能查询出来，部分不能
                //取差集 传入的参数 - 缓存中查出来的 = 缓存中没有的
                List<SimpleGoodsInfo> left = parseCachedGoodsInfo(cachedSimpleGoodsInfo);
                Collection<Long> subtractIds = CollectionUtils.subtract(
                                            goodIds.stream()
                                                    .map(g -> Long.valueOf(g.toString()))
                                                    .collect(Collectors.toList()),
                                            left.stream()
                                                    .map(s -> s.getId())
                                                    .collect(Collectors.toList())
                );
                //缓存中没有的，查询数据表并缓存
                List<SimpleGoodsInfo> right = queryFromDBAndCachedToRedis(
                                            new TableId(
                                                    subtractIds.stream()
                                                            .map(TableId.Id::new).collect(Collectors.toList())
                                            )
                );

                //合并 left 和 right 并返回
                return new ArrayList<>(CollectionUtils.union(left, right));
            }
        } else {
            return queryFromDBAndCachedToRedis(tableId);
        }
    }

    /**
     * 将缓存中的简单商品信息反序列化成 Java Pojo 对象
     * */
    private List<SimpleGoodsInfo> parseCachedGoodsInfo(List<Object> cachedSimpleGoodsInfo) {
        return cachedSimpleGoodsInfo.stream()
                .map(s -> JSON.parseObject(s.toString(), SimpleGoodsInfo.class))
                .collect(Collectors.toList());
    }

    /**
     * 从数据库中查询商品信息后，缓存到 redis 中
     * */
    private List<SimpleGoodsInfo> queryFromDBAndCachedToRedis(TableId tableId) {
        List<Long> ids = tableId.getIds().stream()
                                    .map(TableId.Id::getId)
                                    .collect(Collectors.toList());
        log.info("get simple goods info by ids: [{}]", JSON.toJSONString(ids));
        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(goodsDao.findAllById(ids));
        List<SimpleGoodsInfo> result = ecommerceGoods.stream()
                                        .map(EcommerceGoods::toSimple)
                                        .collect(Collectors.toList());

        //将结果缓存
        log.info("cache goods info: [{}]", JSON.toJSONString(ids));
        Map<String, String> id2jsonObject = new HashMap<>(result.size());

        result.forEach(g -> {
            id2jsonObject.put(g.getId().toString(), JSON.toJSONString(g));
        });

        //保存到redis中
        redisTemplate.opsForHash().putAll(
                GoodsConstant.ECOMMERCE_GOODS_DICT_KEY,
                id2jsonObject
        );

        return result;
    }

    @Override
    public boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories) {
        deductGoodsInventories.forEach((d -> {
            if(d.getCount() <= 0) {
                throw new RuntimeException("purchase goods count need > 0");
            }
        }));

        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(goodsDao.findAllById(
                deductGoodsInventories.stream().map(d -> d.getGoodsId()).collect(Collectors.toList())
        ));
        //根据传递的 goodsIds 查不到商品对象
        if(CollectionUtils.isEmpty(ecommerceGoods)) {
            throw new RuntimeException("can not find any goods by request");
        }
        if(ecommerceGoods.size() != deductGoodsInventories.size()) {
            throw new RuntimeException("request is not valid");
        }

        //Function.identity是集合元素自身
        Map<Long, DeductGoodsInventory> goodsId2Inventory = deductGoodsInventories.stream().collect(
                Collectors.toMap(DeductGoodsInventory::getGoodsId, Function.identity())
        );

        //检查是不是可以扣除库存
        ecommerceGoods.forEach(g -> {
            Long currentInventory = g.getInventory();
            Integer needDeductInventory = goodsId2Inventory.get(g.getId()).getCount();
            //库存不足
            if(currentInventory < needDeductInventory) {
                log.error("goods inventory is not enough: [{}], [{}]",
                        currentInventory, needDeductInventory);
                throw new RuntimeException("goods inventory is not enough: " + g.getId());
            }
            //扣减库存
            g.setInventory(currentInventory - needDeductInventory);
            log.info("deduct goods inventory: [{}], [{}], [{}]", g.getId(),
                    currentInventory, g.getInventory());
        });

        goodsDao.saveAll(ecommerceGoods);
        log.info("deduct goods inventory done");

        return true;
    }
}
