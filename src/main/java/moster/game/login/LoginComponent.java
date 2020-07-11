package moster.game.login;

import moster.app.SimulatorContext;
import moster.app.config.LoginConfig;
import moster.game.player.PlayerComponent;
import org.springframework.core.annotation.Order;

import java.util.Map;

/**
 * @author zhangfei
 */
@Order(0)
public class LoginComponent extends PlayerComponent {

    /**
     * 当前的登入状态。
     */
    private volatile LoginState state = LoginState.UNINITIALIZED;

    /**
     * 向登入服或网关服发起认证的时间。
     */
    private long authRequestTime;

    /**
     * 登入服主机名。
     */
    private String loginHost;

    /**
     * 登入服端口。
     */
    private int loginPort;

    /**
     * 登入服认证成功后返回的key，用于登入网关。
     */
    private String authKey;

    /**
     * 网关服的主机名。
     */
    private String gateHost;

    /**
     * 网关服的端口。
     */
    private int gatePort;

    /**
     * 登入使用的账号名。
     */
    private String account;

    /**
     * 登入账号对应的平台类型。
     */
    private String platformType;

    /**
     * 登入的游戏服的id。
     */
    private int serverId;

    @Override
    @SuppressWarnings("unchecked")
    public boolean load(Object ctx) {
        this.account = ((Map<String, String>) ctx).get("accountName");

        LoginConfig loginConfig = SimulatorContext.getGlobalConfig().getLoginConfig();
        this.platformType = loginConfig.getPlatform();
        this.serverId = loginConfig.getServerId();
        this.loginHost = loginConfig.getHost();
        this.loginPort = loginConfig.getPort();

        return true;
    }

    public LoginState getState() {
        return this.state;
    }

    public void setState(LoginState state) {
        this.state = state;
    }

    public long getAuthRequestTime() {
        return this.authRequestTime;
    }

    public void setAuthRequestTime(long authRequestTime) {
        this.authRequestTime = authRequestTime;
    }

    public String getLoginHost() {
        return this.loginHost;
    }

    public void setLoginHost(String loginHost) {
        this.loginHost = loginHost;
    }

    public int getLoginPort() {
        return this.loginPort;
    }

    public void setLoginPort(int loginPort) {
        this.loginPort = loginPort;
    }

    public String getAuthKey() {
        return this.authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getGateHost() {
        return this.gateHost;
    }

    public void setGateHost(String gateHost) {
        this.gateHost = gateHost;
    }

    public int getGatePort() {
        return this.gatePort;
    }

    public void setGatePort(int gatePort) {
        this.gatePort = gatePort;
    }

    public String getPlatformType() {
        return this.platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getServerId() {
        return this.serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

}

