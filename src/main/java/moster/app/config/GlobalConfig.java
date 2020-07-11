package moster.app.config;

import java.util.List;

/**
 * 模拟器的全局配置。
 *
 * @author zhangfei
 */
public class GlobalConfig {

    private LoginConfig loginConfig;

    /**
     * 字典配置文件所在目录。
     */
    private String dictHome;

    /**
     * 用于账号登入、注册的平台地址。
     */
    private String platformUrl;

    /**
     * 测试账号名称的前缀。
     */
    private String accountPrefix;

    /**
     * 每次执行登入操作的人数限制。
     * 也就是某一时刻允许的最大登入人数。
     */
    private int loginThreshold;

    /**
     * 所有的测试流程配置。
     */
    private List<TestFlowConfig> testFlows;

    public TestFlowConfig.Workflow getWorkflow(String name) {
        for (TestFlowConfig testFlow : this.testFlows) {
            if (testFlow.getName().equals(name)) {
                return testFlow.getWorkflow();
            }
        }
        return null;
    }

    public LoginConfig getLoginConfig() {
        return this.loginConfig;
    }

    public void setLoginConfig(LoginConfig loginConfig) {
        this.loginConfig = loginConfig;
    }

    public String getDictHome() {
        return this.dictHome;
    }

    public void setDictHome(String dictHome) {
        this.dictHome = dictHome;
    }

    public List<TestFlowConfig> getTestFlows() {
        return testFlows;
    }

    public void setTestFlows(List<TestFlowConfig> testFlows) {
        this.testFlows = testFlows;
    }

    public String getPlatformUrl() {
        return platformUrl;
    }

    public void setPlatformUrl(String platformUrl) {
        this.platformUrl = platformUrl;
    }

    public String getAccountPrefix() {
        return accountPrefix;
    }

    public void setAccountPrefix(String accountPrefix) {
        this.accountPrefix = accountPrefix;
    }

    public int getLoginThreshold() {
        return loginThreshold;
    }

    public void setLoginThreshold(int loginThreshold) {
        this.loginThreshold = loginThreshold;
    }

}
