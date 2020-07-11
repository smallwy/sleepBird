package moster.game.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:Gene
 * @Date:2020/1/10 17:54
 * @Description: 商店
 */
public class Shop {

    Shop(int shopId){
        this.shopId = shopId;
    }

    /**
     * 商店类型
     */
    int shopId;

    /**
     * 商品列表
     */
    Map<Integer,Integer> shopItemIds = new HashMap<>();

    /**
     * 刷新时间点
     */
    int refreshSecond;

    /**
     * 刷新价格
     */
    int refreshMoney;

    /**
     * 已经刷新次数
     */
    int refreshNum;
}
