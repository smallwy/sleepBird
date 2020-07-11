package moster.game.login;

import moster.game.player.Player;
import moster.game.player.PlayerSystem;
import moster.infras.core.context.ServerContext;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import moster.infras.core.remote.RemoteEndpoint;
import moster.infras.core.remote.RemoteEndpointFactory;
import moster.infras.util.SecurityUtils;
import com.gameart.proto.gate.GateProto;
import com.gameart.proto.login.LoginProto;
import com.gameart.proto.message.CommonProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * @author zhangfei
 */
@Component
public class LoginSystem implements ISystem, RegisteredMessageCommand {

    private static final String ENCODE_CONTENT = "eoro30493eroeirfkldkf[deprer";

    private static final Logger logger = LoggerFactory.getLogger("PLAYER");

    @Autowired
    private PlayerSystem playerSystem;

    /**
     * 请求与登入服建立连接。
     */
    public void connectLoginServer(Player player) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);

        RemoteEndpointFactory factory = ServerContext.getBean("loginRemoteEndpointFactory");
        RemoteEndpoint remoteEndpoint = factory.create(loginComponent.getLoginHost(), loginComponent.getLoginPort());
        player.setRemoteEndpoint(remoteEndpoint);

        loginComponent.setState(LoginState.LOGIN_CONNECTING);
    }

    /**
     * 检查正在与登入服建立的连接状态。
     */
    public void checkLoginServerConnect(Player player) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);
        RemoteEndpoint remoteEndpoint = player.getRemoteEndpoint();
        if (remoteEndpoint.isConnected()) {
            remoteEndpoint.bind(player);
            loginComponent.setState(LoginState.LOGIN_CONNECTED);
            logger.debug("玩家[{}]与登入服建立连接成功", loginComponent.getAccount());
        } else if (remoteEndpoint.isDisconnected()) {
            loginComponent.setState(LoginState.FAILURE);
            logger.warn("玩家[{}]与登入服建立连接失败", loginComponent.getAccount());
        }
    }

    /**
     * 向登入服发送认证请求。
     */
    public void sendLoginServerAuth(Player player) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);
        LoginProto.ReqClientLogin.Builder req = LoginProto.ReqClientLogin.newBuilder();

        req.setPlatformType(loginComponent.getPlatformType());
        req.setAccountId(loginComponent.getAccount());
        req.setServerID(loginComponent.getServerId());

        String timeStr = System.currentTimeMillis() + "";
        String result = ENCODE_CONTENT + "|" + timeStr;

        byte[] degist = new byte[0];
        try {
            degist = SecurityUtils.digest(SecurityUtils.MessageDigestType.MD5, result.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String md5 = SecurityUtils.byte2Hex(degist);

        req.setEncodeKey(md5);
        req.setTime(timeStr);

        player.getRemoteEndpoint().send(req);

        loginComponent.setState(LoginState.LOGIN_AUTHENTICATING);
        loginComponent.setAuthRequestTime(System.currentTimeMillis());
    }

    /**
     * 检查登入服认证。
     */
    public void checkLoginServerAuth(Player player) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);
        RemoteEndpoint remoteEndpoint = player.getRemoteEndpoint();
        if (remoteEndpoint.isDisconnected()) {
            loginComponent.setState(LoginState.FAILURE);
            logger.warn("玩家[{}]在检查登入认证状态时发现连接被断开", loginComponent.getAccount());
            return;
        }

        // 如果15秒钟还没有收到登入服的响应，就认为登入失败了。
        long authRequestTime = loginComponent.getAuthRequestTime();
        long currTime = System.currentTimeMillis();
        if (currTime - authRequestTime >= 15 * 1000L) {
            loginComponent.setState(LoginState.FAILURE);
            logger.warn("玩家[{}]登入服验证超时", loginComponent.getAccount());
        }
    }

    /**
     * 收到登入服的登入响应。
     */
    @InboundMessageCommand(commandId = 5002, desc = "登入服认证")
    public void receiveLoginServerResponse(Player player, LoginProto.RespClientLogin resp) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);

        int result = resp.getResult();
        if (result == 1) {
            loginComponent.setState(LoginState.LOGIN_AUTHENTICATED);
            loginComponent.setGateHost(resp.getGateHost());
            loginComponent.setGatePort(resp.getGatePort());
            loginComponent.setAuthKey(resp.getAuthKey());
            logger.debug("玩家[{}]收到登入服成功的认证结果", loginComponent.getAccount());

            // 主动断开与login-server的连接。
            player.getRemoteEndpoint().close(1, "登入认证结束");
        }

        else {
            loginComponent.setState(LoginState.FAILURE);
            logger.warn("玩家[{}]收到登入服失败的认证结果[{}]", loginComponent.getAccount(), result);
            player.getRemoteEndpoint().close(1, "登入认证结束");
        }
    }

    /**
     * 请求与网关服建立连接。
     */
    public void connectGateServer(Player player) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);
        RemoteEndpointFactory factory = ServerContext.getBean("gateRemoteEndpointFactory");

        RemoteEndpoint remoteEndpoint = factory.create(loginComponent.getGateHost(), loginComponent.getGatePort());
        player.setRemoteEndpoint(remoteEndpoint);

        loginComponent.setState(LoginState.GATE_CONNECTING);
    }

    /**
     * 检查正在与网关服建立的连接状态。
     */
    public void checkGateServerConnect(Player player) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);
        RemoteEndpoint remoteEndpoint = player.getRemoteEndpoint();
        if (remoteEndpoint.isConnected()) {
            remoteEndpoint.bind(player);
            loginComponent.setState(LoginState.GATE_CONNECTED);
            logger.debug("玩家[{}]与网关服建立连接成功", loginComponent.getAccount());

        } else if (remoteEndpoint.isDisconnected()) {
            loginComponent.setState(LoginState.FAILURE);
            logger.warn("玩家[{}]与网关服建立连接失败", loginComponent.getAccount());
        }
    }

    /**
     * 向网关服发送认证请求。
     */
    public void sendGateServerAuth(Player player) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);

        GateProto.ReqClientLoginToGate.Builder request = GateProto.ReqClientLoginToGate.newBuilder();
        request.setPlatformType(loginComponent.getPlatformType());
        request.setAccountId(loginComponent.getAccount());
        request.setAuthKey(loginComponent.getAuthKey());
        player.getRemoteEndpoint().send(request);

        loginComponent.setState(LoginState.GATE_AUTHENTICATING);
        loginComponent.setAuthRequestTime(System.currentTimeMillis());
    }

    /**
     * 检查网关服认证。
     */
    public void checkGateServerAuth(Player player) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);
        RemoteEndpoint remoteEndpoint = player.getRemoteEndpoint();
        if (remoteEndpoint.isDisconnected()) {
            loginComponent.setState(LoginState.FAILURE);
            logger.warn("玩家[{}]在检查网关认证状态时发现连接被断开", loginComponent.getAccount());
            return;
        }

        // 如果在一段时间内还没有收到网关服的响应，就认为登入失败了。
        long authRequestTime = loginComponent.getAuthRequestTime();
        long currTime = System.currentTimeMillis();
        if (currTime - authRequestTime >= 30 * 1000L) {
            loginComponent.setState(LoginState.FAILURE);
            logger.warn("玩家[{}]网关验证超时", loginComponent.getAccount());
        }
    }

    /**
     * 收到网关服的登入响应。
     */
    @InboundMessageCommand(commandId = 1002, desc = "网关服登入响应")
    public void receiveGateServerResponse(Player player, GateProto.RespClientLoginToGame resp) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);

        int result = resp.getResult();
        if (result == 1) {
            loginComponent.setState(LoginState.GATE_AUTHENTICATED);
            logger.debug("玩家[{}]收到网关服成功的认证结果", loginComponent.getAccount());

        } else {
            loginComponent.setState(LoginState.FAILURE);
            logger.warn("玩家[{}]收到网关服失败的认证结果[{}]", loginComponent.getAccount(), result);
        }
    }

    /**
     * 收到game下发登入消息完成的标识。
     */
    @InboundMessageCommand(commandId = 10036, desc = "登入下发结束")
    public void receiveCompleteNotice(Player player, CommonProto.RespCompleteNotice resp) {
        LoginComponent loginComponent = player.getComponent(LoginComponent.class);
        loginComponent.setState(LoginState.SUCCESS);
    }

    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(LoginProto.ReqClientLogin.class, 5001, "登入服认证");
        RegisteredMessageCommand.registerOutboundMessage(GateProto.ReqClientLoginToGate.class, 1001, "请求网关登入");
    }

}
