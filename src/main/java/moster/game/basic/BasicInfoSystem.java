package moster.game.basic;

import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.CommonProto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author zhangfei
 */
@Component
public class BasicInfoSystem implements ISystem, RegisteredMessageCommand {

    /**
     * 收到KV通知。
     */
    @InboundMessageCommand(commandId = 10034, desc = "登入通知")
    public void receiveKeyValueNotice(Player player, CommonProto.RespKeyValueNotice resp) {
        BasicInfoComponent basicInfoComponent = player.getComponent(BasicInfoComponent.class);
        for (CommonProto.KeyValue keyValue : resp.getKeyValueList()) {
            int type = keyValue.getKey();
            String value = keyValue.getValue();

            if (type == 0) {
                basicInfoComponent.level = Integer.parseInt(value);

            } else if (type == 1) {
                basicInfoComponent.fightValue = Integer.parseInt(value);

            } else if (type == 2) {
                basicInfoComponent.playerId = Long.parseLong(value);

            } else if (type == 3) {
                basicInfoComponent.playerName = value;

            } else if (type == 4) {
                basicInfoComponent.headIcon = Integer.parseInt(value);

            } else if (type == 5) {
                basicInfoComponent.headIconFrame = Integer.parseInt(value);

            } else if (type == 8) {
                basicInfoComponent.guildId = Long.parseLong(value);
            }
        }
    }

    /**
     * 请求改名。
     */
    public void changeName(Player player, String newName) {
        CommonProto.ReqChangePlayerInfo.Builder req = CommonProto.ReqChangePlayerInfo.newBuilder();
        req.setName(newName);
        req.setStatement("我是机器人-" + newName);
        player.send(req);
    }

    /**
     * 请求修改头像。
     */
    public void changeHeadIcon(Player player, int headIcon, int headIconFrame) {
        CommonProto.ReqChangePlayerInfo.Builder req = CommonProto.ReqChangePlayerInfo.newBuilder();
        req.setHeadIcon(headIcon);
        req.setHeadIconFrame(headIconFrame);
        player.send(req);
    }

    /**
     * 收到修改自身基础信息的响应。
     */
    @InboundMessageCommand(commandId = 10044, desc = "修改玩家信息")
    public void receiveChangeInfo(Player player, CommonProto.RespChangePlayerInfo resp) {
        BasicInfoComponent basicInfoComponent = player.getComponent(BasicInfoComponent.class);

        String name = resp.getName();
        if (!StringUtils.isEmpty(name)) {
            basicInfoComponent.playerName = name;
        }

        String statement = resp.getStatement();
        if (!StringUtils.isEmpty(statement)) {
            basicInfoComponent.statement = statement;
        }

        int headIcon = resp.getHeadIcon();
        if (headIcon != 0) {
            basicInfoComponent.headIcon = headIcon;
        }

        int headIconFrame = resp.getHeadIconFrame();
        if (headIconFrame != 0) {
            basicInfoComponent.headIconFrame = headIconFrame;
        }
    }

    /**
     * 查看玩家信息。
     *
     * @param targetId 值为0表示查看自己，否则表示查看他人
     */
    public void getPlayerInfo(Player player, long targetId) {
        CommonProto.ReqPlayerInfo.Builder req = CommonProto.ReqPlayerInfo.newBuilder();
        req.setPlayerId(targetId);
        req.setWay(1);
        player.send(req);
    }

    /**
     * 收到查看的玩家信息。
     */
    @InboundMessageCommand(commandId = 10042, desc = "查看玩家信息")
    public void receivePlayerInfo(Player player, CommonProto.RespPlayerInfo resp) {
        long targetId = resp.getPlayerId();
        if (targetId != 0) {
            return;
        }

        BasicInfoComponent basicInfoComponent = player.getComponent(BasicInfoComponent.class);
        basicInfoComponent.headIcon = resp.getCurrHeadIcon();
        basicInfoComponent.headIconFrame = resp.getCurrHeadIconFrame();

        basicInfoComponent.headIcons.clear();
        for (CommonProto.IconNew iconNew : resp.getHeadIconsList()) {
            basicInfoComponent.headIcons.add(iconNew.getId());
        }

        basicInfoComponent.headIconFrames.clear();
        for (CommonProto.IconNew iconNew : resp.getHeadIconFramesList()) {
            basicInfoComponent.headIconFrames.add(iconNew.getId());
        }

        basicInfoComponent.vip = resp.getVipLevel();
        basicInfoComponent.guildId = resp.getClanId();
        basicInfoComponent.fightValue = resp.getFightValue();
        basicInfoComponent.level = resp.getLevel();
        basicInfoComponent.exp = resp.getExp();
        basicInfoComponent.title = resp.getTitle();
    }

    /**
     * 收到所有货币信息。
     */
    @InboundMessageCommand(commandId = 10022, desc = "所有货币信息")
    public void receiveAllCurrencies(Player player, CommonProto.RespGetCurrencies resp) {
        BasicInfoComponent basicInfoComponent = player.getComponent(BasicInfoComponent.class);
        basicInfoComponent.currencies.clear();
        basicInfoComponent.currencies.addAll(resp.getValuesList());
    }

    /**
     * 收到某项货币更新消息。
     */
    @InboundMessageCommand(commandId = 10026, desc = "某项货币更新")
    public void receiveCurrencyUpdate(Player player, CommonProto.RespCurrencyUpdate resp) {
        BasicInfoComponent basicInfoComponent = player.getComponent(BasicInfoComponent.class);

        // 有可能还没有初始化所有货币信息，这时收到了某项货币更新消息，比如登入时收到体力变化通知。
        if (basicInfoComponent.currencies.size() > resp.getCurrencyIndex()) {
            basicInfoComponent.currencies.set(resp.getCurrencyIndex(), resp.getNum());
        }
    }

    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(CommonProto.ReqChangePlayerInfo.class, 10043, "修改玩家信息");
        RegisteredMessageCommand.registerOutboundMessage(CommonProto.ReqPlayerInfo.class, 10041, "查看玩家信息");
    }

}
