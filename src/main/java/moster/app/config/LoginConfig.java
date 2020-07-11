package moster.app.config;

/**
 * 登入服相关配置。
 *
 * @author zhangfei
 */
public class LoginConfig {

    /**
     * 登入服主机地址。
     */
    private String host;

    /**
     * 登入服主机端口。
     */
    private int port;

    /**
     * 登入的游戏服id。
     */
    private int serverId;

    /**
     * 平台类型。
     */
    private String platform;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
