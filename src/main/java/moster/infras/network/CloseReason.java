package moster.infras.network;

/**
 * @author zhangfei
 */
public enum CloseReason {

    //region 网络原因
    /**
     * 远端主动断开。
     */
    REMOTE_ACTIVE(1, "远端主动断开"),

    /**
     * 连接出现异常。
     * 比如消息解码错误、I/O异常等。
     */
    CHANNEL_EXCEPTION(2, "连接出现异常"),
    //endregion

    //region 业务原因
    /**
     * 登入认证超时。
     */
    LOGIN_AUTH_TIMEOUT(101, "登入认证超时"),

    /**
     * 登入认证失败。
     */
    LOGIN_AUTH_FAILURE(102, "登入认证失败"),

    /**
     * 心跳检测失败。
     */
    HEARTBEAT(103, "心跳检测失败"),
    //endregion
    ;

    public final int id;

    public final String desc;

    CloseReason(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

}
