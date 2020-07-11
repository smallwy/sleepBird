package moster.game.login;

/**
 * @author zhangfei
 */
public enum LoginState {

    /**
     * 登入失败。
     */
    FAILURE,

    /**
     * 未初始化，这个是初始状态。
     */
    UNINITIALIZED,

    /**
     * 正在连接登入服。
     */
    LOGIN_CONNECTING,

    /**
     * 登入服已经连上。
     */
    LOGIN_CONNECTED,

    /**
     * 登入服正在验证。
     */
    LOGIN_AUTHENTICATING,

    /**
     * 登入服通过验证。
     */
    LOGIN_AUTHENTICATED,

    /**
     * 正在连接网关。
     */
    GATE_CONNECTING,

    /**
     * 网关已经连接上。
     */
    GATE_CONNECTED,

    /**
     * 登入服正在验证。
     */
    GATE_AUTHENTICATING,

    /**
     * 登入服通过验证。
     */
    GATE_AUTHENTICATED,

    /**
     * 登入成功。
     */
    SUCCESS,
    ;

}
