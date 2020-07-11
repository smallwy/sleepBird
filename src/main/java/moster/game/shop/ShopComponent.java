package moster.game.shop;

import moster.game.player.PlayerComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家的商店组件。
 *
 * @author Gene
 */
public class ShopComponent extends PlayerComponent {

    /**
     * 当前访问的商店
     */
    int shopId;

    Map<Integer,Shop> shops = new HashMap<>();

    Shop getShop(int shopId){
        return this.shops.computeIfAbsent(shopId, (k) -> new Shop(shopId));
    }

}
