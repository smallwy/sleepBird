package moster.game.player;

import moster.infras.core.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 玩家掉线事件。
 * TODO zhangfei 改名叫ClientDisconnectEvent or PlayerDisconnectEvent?
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class PlayerOfflineEvent implements Runnable {

    /**
     * 离线的玩家。
     */
    private final Player player;

    /**
     * 离线原因标识。
     */
    private final int reason;

    /**
     * 离线原因描述。
     */
    private final String desc;

    @Autowired
    private PlayerSystem playerSystem;

    @Override
    public void run() {
        this.playerSystem.playerLogout(this.player, false, this.reason, this.desc);
    }

    public PlayerOfflineEvent(Client client, int reason, String desc) {
        this.player = (Player) client;
        this.reason = reason;
        this.desc = desc;
    }

}
