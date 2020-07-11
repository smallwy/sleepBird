package moster.game.testflow;

/**
 * @author zhangfei
 */
public class TestFlowPlayerInfo {

    /**
     * 账号名称。
     */
    String accountName;

    /**
     * 绑定的测试流程名称。
     */
    String testFlowName;

    /**
     * 离线时间。
     */
    long offlineTime;

    /**
     * 当前执行的工作流程次数。
     */
    int currWorkFlowTimes;

    /**
     * 执行的工作流程次数的上限。
     */
    int maxWorkFlowTimes;

}
