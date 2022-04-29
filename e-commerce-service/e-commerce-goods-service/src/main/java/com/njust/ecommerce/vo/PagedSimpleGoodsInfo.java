package com.njust.ecommerce.vo;

import com.njust.ecommerce.goods.SimpleGoodsInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页商品信息
 * */
@ApiModel(description = "分页商品信息对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedSimpleGoodsInfo {

    @ApiModelProperty(value = "分页简单商品信息")
    private List<SimpleGoodsInfo> simpleGoodsInfos;

    @ApiModelProperty(value = "是否还有更多的商品分页")
    private boolean hasMore;
}


