package moster.game.platform;

import moster.game.player.PlayerComponent;

import java.util.Map;

/**
 * 玩家的平台组件。
 *
 * @author zhangfei
 */
public class PlatformComponent extends PlayerComponent {

    /**
     * 登入平台的账号名称。
     */
    private String username;

    /**
     * 登入平台的账号密码。
     */
    private String password;

    /**
     * 登入成功后平台返回的一个唯一标识。
     */
    private String openId;

    /**
     * 平台账号的登入状态。
     *
     * <pre>
     * -1: 登入失败
     * -2: 账号未注册
     *  0: 未初始化
     *  1: 登入成功
     *  2: 正在登入
     * </pre>
     */
    private int signInStatus;

    /**
     * <pre>
     * 平台账号的注册状态。
     *
     * -1: 注册失败
     *  0: 未初始化
     *  1: 注册成功
     *  2: 正在注册
     * </pre>
     */
    private int signUpStatus;

    @Override
    @SuppressWarnings("unchecked")
    public boolean load(Object ctx) {
        this.username = ((Map<String, String>) ctx).get("accountName");
        this.password = "11111111";
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getSignInStatus() {
        return signInStatus;
    }

    public void setSignInStatus(int signInStatus) {
        this.signInStatus = signInStatus;
    }

    public int getSignUpStatus() {
        return signUpStatus;
    }

    public void setSignUpStatus(int signUpStatus) {
        this.signUpStatus = signUpStatus;
    }
}

