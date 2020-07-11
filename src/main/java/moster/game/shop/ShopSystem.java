package moster.game.shop;

import com.gameart.dict.DictManager;
import com.gameart.dict.data.shop.ShopCommonDictEntry;
import com.gameart.dict.data.shop.ShopConfigDict;
import com.gameart.dict.data.shop.ShopConfigDictEntry;
import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.ArenaProto;
import com.gameart.proto.message.ShopProto;
import io.netty.util.internal.ThreadLocalRandom;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商店系统。
 *
 * @author Gene
 */
@Component
public class ShopSystem implements ISystem, RegisteredMessageCommand {


    /**
     * 请求商品列表
     */
    public void getInfo(Player player) {
        Map<Integer, ShopConfigDictEntry> all = DictManager.getInstance().getDict(ShopConfigDict.class).getAll();
        ShopProto.ReqShopShowItem.Builder req = ShopProto.ReqShopShowItem.newBuilder();
        ShopComponent shopComponent = player.getComponent(ShopComponent.class);
        int random = random(all.keySet());
        shopComponent.shopId = random;
        req.setShopId(random);
        player.send(req);
    }
    /**
     * 请求商品详细信息
     */
    public void getDetailedInfo(Player player) {
        ShopProto.ReqShopItemDetailed.Builder req = ShopProto.ReqShopItemDetailed.newBuilder();
        ShopComponent shopComponent = player.getComponent(ShopComponent.class);
        req.setShopId(shopComponent.shopId);
        player.send(req);
    }
    /**
     * 请求购买商品
     */
    public void buy(Player player) {
        ShopComponent shopComponent = player.getComponent(ShopComponent.class);
        Shop shop = shopComponent.getShop(shopComponent.shopId);
        int random = random(shop.shopItemIds.keySet());
        ShopProto.ReqShopBuyItem.Builder req = ShopProto.ReqShopBuyItem.newBuilder();
        req.setShopItemId(random);
        int num = ThreadLocalRandom.current().nextInt(100);
        int i = ThreadLocalRandom.current().nextInt(11);
        num = i > 2 ? num : -num;
        req.setNum(num);
        player.send(req);
    }
    /**
     * 请求刷新商品
     */
    public void refresh(Player player) {
        ShopComponent shopComponent = player.getComponent(ShopComponent.class);
        ShopProto.ReqShopRefresh.Builder req = ShopProto.ReqShopRefresh.newBuilder();
        req.setShopId(shopComponent.shopId);
        player.send(req);
    }


    /**
     * /返回商品列表
     */
    @InboundMessageCommand(commandId = 11602)
    public void shopPane(Player player, ShopProto.RespShopShowItem resp) {
        ShopComponent shopComponent = player.getComponent(ShopComponent.class);
        int shopId = resp.getShopId();
        Shop shop = shopComponent.getShop(shopId);
        shop.refreshSecond = resp.getRefreshSecond();
        shop.refreshMoney = resp.getRefreshMoney();
        shop.refreshNum = resp.getRefreshNum();
    }

    /**
     * 返回购买商品。
     */
    @InboundMessageCommand(commandId = 11604)
    public void buy(Player player, ShopProto.ReqShopBuyItem resp) {
        ShopCommonDictEntry shopCommonDictEntry = ShopCommonDictEntry.get(resp.getShopItemId());
        ShopComponent shopComponent = player.getComponent(ShopComponent.class);
        Shop shop = shopComponent.getShop(shopCommonDictEntry.shopId);
        shop.shopItemIds.put(shopCommonDictEntry.idx, resp.getNum());
    }
    /**
     * 返回商品详细信息
     */
    @InboundMessageCommand(commandId = 11608)
    public void itemsDetailed(Player player, ShopProto.RespShopItemDetailed resp) {
        ShopComponent shopComponent = player.getComponent(ShopComponent.class);
        int shopId = resp.getShopId();
        Shop shop = shopComponent.getShop(shopId);
        List<ShopProto.ShopItem> shopItemsList = resp.getShopItemsList();
        shop.shopItemIds.clear();
        for (ShopProto.ShopItem shopItem : shopItemsList) {
            shop.shopItemIds.put(shopItem.getShopItemId(),shopItem.getAmount());
        }
    }

    @Override
    public void registerOutboundMessages() {
//        RegisteredMessageCommand.registerOutboundMessage(ShopProto.ReqShopShowItem.class, 11601);
//        RegisteredMessageCommand.registerOutboundMessage(ShopProto.ReqShopBuyItem.class, 11603);
//        RegisteredMessageCommand.registerOutboundMessage(ShopProto.ReqShopRefresh.class, 11605);
//        RegisteredMessageCommand.registerOutboundMessage(ShopProto.ReqShopItemDetailed.class, 11607);
    }

    public static int random(Set<Integer> set){
        int size = set.size();
        int index = ThreadLocalRandom.current().nextInt(size);
        Iterator<Integer> iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Integer next =  iterator.next();
            if(i == index)
                return next;
            i++;
        }
        return 0;
    }

}
