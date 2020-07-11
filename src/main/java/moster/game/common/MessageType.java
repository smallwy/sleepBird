package moster.game.common;

import com.gameart.proto.message.CardProto.ReqRandCard;
import com.gameart.proto.message.CardProto.RespRandCard;
import com.gameart.proto.message.ClimbPowerProto;
import com.google.protobuf.AbstractMessage;

import java.util.HashMap;
import java.util.Map;

import static com.gameart.proto.message.ClimbPowerProto.*;

public enum MessageType {
    REQ_RANDOM_CARD(10303, ReqRandCard.class,"开始抽卡"),
    RESP_REAMDOM_CARD(10304, RespRandCard.class,"抽卡返回"),

    REQ_CLIMB_TOWER(13001,ReqTowerInfo .class,"爬塔信息"),
    RESP_CLIMB_TOWER(13002, RespTowerInfo.class,"爬塔信息返回"),

    REQ_TOWER_FIGHT(13003,ReqTowerFight.class,"爬塔战斗开始"),
    RESP_TOWER_FIGHT(13004, RespTowerFight.class,"爬塔战斗返回");

    private int commandId;
    private Class<? extends AbstractMessage> message;

    private String param;

    MessageType(int commandId, Class<? extends AbstractMessage> message,String param) {
        this.commandId = commandId;
        this.message = message;
        this.param=param;
    }

    private static Map<Integer, Class<? extends AbstractMessage>> idToClassMap = new HashMap<>();

    static {
        for (MessageType type : values()) {
            idToClassMap.put(type.getCommandId(), type.getMessage());
        }
    }


    public static Class<? extends AbstractMessage> valueof(int commandId) {
        return idToClassMap.get(commandId);
    }
    public int getCommandId() {
        return commandId;
    }

    public Class<? extends AbstractMessage> getMessage() {
        return message;
    }

    public String getParam() {
        return param;
    }

}
