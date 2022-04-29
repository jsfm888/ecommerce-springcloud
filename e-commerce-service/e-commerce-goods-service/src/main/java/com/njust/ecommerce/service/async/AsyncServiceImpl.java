package com.njust.ecommerce.service.async;

import com.alibaba.fastjson.JSON;
import com.njust.ecommerce.constant.GoodsConstant;
import com.njust.ecommerce.dao.EcommerceGoodsDao;
import com.njust.ecommerce.entity.EcommerceGoods;
import com.njust.ecommerce.goods.GoodsInfo;
import com.njust.ecommerce.goods.SimpleGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AsyncServiceImpl implements IAsyncService {

    private final StringRedisTemplate stringRedisTemplate;
    private final EcommerceGoodsDao goodsDao;

    public AsyncServiceImpl(StringRedisTemplate stringRedisTemplate, EcommerceGoodsDao goodsDao) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.goodsDao = goodsDao;
    }

    /**
     * 异步任务需要加上注解，并指定要使用的线程池
     * 异步任务处理两件事
     * 1. 保存商品信息到数据库
     * 2. 更新 redis 中缓存的商品信息
     * */
    @Async("getAsyncExecutor")
    @Override
    public void asyncImportGoods(List<GoodsInfo> goodsInfos, String taskId) {

        log.info("async task running taskId: [{}]", taskId);
        //开启一个计时器
        StopWatch watch = StopWatch.createStarted();

        //1. 如果 goodsInfos 中存在重复（分类、品牌和商品名称全部一样）的商品，不保存; 直接返回，记录错误日志
        //请求数据是否合法的标记
        boolean isIllegal = false;

        //将商品信息 joint 在一起，用于判断商品是否重复
        Set<String> goodsJointInfos = new HashSet<>(goodsInfos.size());
        //用于存放过滤后的可以入库的商品信息
        List<GoodsInfo> filteredGoodsInfos = new ArrayList<>(goodsInfos.size());

        for(GoodsInfo goodsInfo : goodsInfos) {
            //基本的判断
            if(goodsInfo.getPrice() < 0 || goodsInfo.getSupply() < 0) {
                log.info("goods info is invalid: [{}]", JSON.toJSONString(goodsInfo));
                continue;
            }
            //组合商品信息
            String goodsJointInfo = String.format(
                    "%s:%s:%s",
                    goodsInfo.getGoodsCategory(), goodsInfo.getBrandCategory(),
                    goodsInfo.getGoodsName()
            );
            if(goodsJointInfos.contains(goodsJointInfo)) {
                isIllegal = true;
            }
            //加入到两个容器中
            goodsJointInfos.add(goodsJointInfo);
            filteredGoodsInfos.add(goodsInfo);
        }

        //如果存在重复商品或者没有需要入库的商品 直接打印日志并返回
        if(isIllegal || CollectionUtils.isEmpty(filteredGoodsInfos)) {
            watch.stop();
            log.error("import nothing: [{}]", JSON.toJSONString(filteredGoodsInfos));
            log.info("check and import goods done: [{}]ms", watch.getTime(TimeUnit.MILLISECONDS));
            return;
        }

        List<EcommerceGoods> ecommerceGoods = filteredGoodsInfos.stream()
                                        .map(EcommerceGoods::to)
                                        .collect(Collectors.toList());

        List<EcommerceGoods> targetGoods = new ArrayList<>(ecommerceGoods.size());
        //2. 保存 goodsInfo 之前，先判断数据库中是否存在重复的商品
        ecommerceGoods.forEach(g -> {
            if(null != goodsDao.findFirst1ByGoodsCategoryAndBrandCategoryAndGoodsName(
                    g.getGoodsCategory(), g.getBrandCategory(), g.getGoodsName()).orElse(null)
            ) {
                //商品重复 直接返回
                return;
            }
            targetGoods.add(g);
        });

        //商品信息入库
        List<EcommerceGoods> savedGoods = IterableUtils.toList(goodsDao.saveAll(targetGoods));

        //保存到redis缓存重
        saveNewGoodsToRedis(savedGoods);


        log.info("saved goods info to db and redis: [{}]", savedGoods.size());
        watch.stop();
        log.info("check and import goods success: [{}ms]",
                watch.getTime(TimeUnit.MILLISECONDS));

    }

    /**
     * 保存简单商品到redis中
     * dict: key -> <id, simpleGoodsInfo(json)>
     * */
    private void saveNewGoodsToRedis(List<EcommerceGoods> savedGoods) {
        //转换成简单商品进行保存
        List<SimpleGoodsInfo> simpleGoodsInfos = savedGoods.stream()
                                                .map(EcommerceGoods::toSimple)
                                                .collect(Collectors.toList());
        Map<String, String> id2jsonObject = new HashMap<>(simpleGoodsInfos.size());

        simpleGoodsInfos.forEach(g -> {
            id2jsonObject.put(g.getId().toString(), JSON.toJSONString(g));
        });

        stringRedisTemplate.opsForHash()
                .putAll(GoodsConstant.ECOMMERCE_GOODS_DICT_KEY,
                        id2jsonObject);
    }
}
