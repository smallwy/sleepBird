package moster.app.config;

import java.util.Map;

/**
 * 测试流程配置。
 *
 * @author zhangfei
 */
public class TestFlowConfig {

    /**
     * 测试流程的名称。
     */
    private String name;

    /**
     * 当前测试流程是否被启用。
     */
    private boolean enabled;

    /**
     * 执行测试流程的玩家总数。
     */
    private int playerTotal;

    /**
     * 重复执行多少次完整的工作流程。
     */
    private int workflowTimes;

    /**
     * 实际的工作流程。
     */
    private Workflow workflow;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPlayerTotal() {
        return playerTotal;
    }

    public void setPlayerTotal(int playerTotal) {
        this.playerTotal = playerTotal;
    }

    public int getWorkflowTimes() {
        return workflowTimes;
    }

    public void setWorkflowTimes(int workflowTimes) {
        this.workflowTimes = workflowTimes;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    /**
     * 测试流程中的工作流程。
     *
     * 一个工作流程由以下部分组成：
     * 1. 首先执行准备动作
     * 2. 执行特定次数的负荷动作
     * 3. 最后执行清理动作
     *
     * 至此，一个完整的工作流程就执行完了。
     */
    public static class Workflow {

        /**
         * 准备动作。
         * 比如一个完整的测试流程中，首先要执行登入操作。
         */
        private String[] prepare;

        /**
         * 负荷动作的权重配置。
         *
         * 比如有这样的配置：arena:100, idle:50，
         * 表示arena动作有(100/150)的几率执行，idle动作有(50/150)的几率执行。
         */
        private Map<String, Integer> workloadWeight;

        /**
         * 负荷动作的重复次数。
         */
        private int workloadTimes;

        /**
         * 清理动作。
         * 当一个测试流程要结束时，需要执行一些清理操作，比如退出操作。
         */
        private String[] clean;

        public String[] getPrepare() {
            return prepare;
        }

        public void setPrepare(String[] prepare) {
            this.prepare = prepare;
        }

        public Map<String, Integer> getWorkloadWeight() {
            return workloadWeight;
        }

        public void setWorkloadWeight(Map<String, Integer> workloadWeight) {
            this.workloadWeight = workloadWeight;
        }

        public int getWorkloadTimes() {
            return workloadTimes;
        }

        public void setWorkloadTimes(int workloadTimes) {
            this.workloadTimes = workloadTimes;
        }

        public String[] getClean() {
            return clean;
        }

        public void setClean(String[] clean) {
            this.clean = clean;
        }
    }

}
