package moster.game.player;

import moster.app.SimulatorContext;
import moster.game.decision.DecisionSystem;
import moster.game.decision.event.ErrorCodeEvent;
import moster.game.platform.PlatformComponent;
import moster.game.testflow.TestFlowSystem;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.executor.EventExecutor;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import moster.infras.core.remote.RemoteEndpoint;
import com.gameart.proto.message.CommonProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家系统。
 *
 * @author zhangfei
 */
@Component
public class PlayerSystem implements ISystem, RegisteredMessageCommand {

    private static final Logger logger = LoggerFactory.getLogger("PLAYER");

    /**
     * 所有的在线玩家，key为账号名，value为玩家实例。
     */
    private final Map<String, Player> onlinePlayers = new ConcurrentHashMap<>();

    /**
     * 用于控制玩家登入速率的令牌桶。
     */
    private FixedNumberTokenBucket tokenBucket;

    @Autowired
    private DecisionSystem decisionSystem;

    @Autowired
    private TestFlowSystem testFlowSystem;

    /**
     * 玩家收到错误码。
     */
    @InboundMessageCommand(commandId = 10002, desc = "错误码")
    public void receiveErrorCode(Player player, CommonProto.RespErrorCode resp) {
        logger.warn("({})收到消息({})的错误码({})", player, resp.getMessageId(), resp.getErrorCode());
        ErrorCodeEvent event = new ErrorCodeEvent(resp.getMessageId(), resp.getErrorCode());
        this.decisionSystem.emit(player, event);
    }

    /**
     * 收到游戏服的心跳回复检查请求。
     */
    @InboundMessageCommand(commandId = 10004, desc = "心跳检测")
    public void heartbeatVerify(Player player, CommonProto.RespHeartbeatCheck resp) {
        CommonProto.ReqHeartbeatCheck.Builder req = CommonProto.ReqHeartbeatCheck.newBuilder();
        req.setCheckDeadline(resp.getCheckDeadline());
        player.send(req);
    }

    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(CommonProto.ReqHeartbeatCheck.class, 10003, "心跳回复");
    }

    @Override
    public boolean init() {
        int capacity = SimulatorContext.getGlobalConfig().getLoginThreshold();
        this.tokenBucket = new FixedNumberTokenBucket(capacity);
        return true;
    }

    /**
     * 驱动所有玩家做出行为决策。
     */
    public void drivePlayers() {
        long currTime = System.currentTimeMillis();
        for (Player player : this.onlinePlayers.values()) {
            this.decisionSystem.drivePlayer(player, currTime);
        }
    }

    /**
     * @return 当前在线的玩家数量
     */
    public int getOnlinePlayersTotal() {
        return this.onlinePlayers.size();
    }

    /**
     * 玩家登入。
     *
     * @param accountName 玩家账号名称
     * @param workflowName 玩家使用的测试流程名称
     */
    public void playerLogin(String accountName, String workflowName) {
        boolean canLogin = this.tokenBucket.tryAcquire();
        if (!canLogin) {
            // 同时登入的人数达到限制，这次就不继续登入了。
            return;
        }

        Player player = PlayerFactory.create(accountName, workflowName);
        if (player == null) {
            logger.error("创建使用测试流程[{}]的玩家[{}]实例失败", accountName, workflowName);
            return;
        }

        onlinePlayers.put(accountName, player);
        this.tokenBucket.release();
    }

    /**
     * 玩家登出。
     *
     * @param player 执行登出操作的玩家
     * @param active 是否主动登出游戏
     * @param reason 登出原因标识
     * @param desc 登出原因描述
     */
    public void playerLogout(Player player, boolean active, int reason, String desc) {
        PlatformComponent platformComponent = player.getComponent(PlatformComponent.class);
        String accountName = platformComponent.getUsername();
        this.onlinePlayers.remove(accountName);

        // 如果是被动离线的，那么网络已经断开了，就不需要再走这一步。
        if (active) {
            RemoteEndpoint remoteEndpoint = player.getRemoteEndpoint();
            if (remoteEndpoint != null) {
                remoteEndpoint.close(reason, desc);
            }
        }

        EventExecutor eventExecutor = player.getEventExecutor();
        if (eventExecutor != null) {
            eventExecutor.release();
        }

        this.testFlowSystem.playerOffline(accountName);
        logger.info("player[{}] logout[{} - {}]", player, reason, desc);
    }

}
