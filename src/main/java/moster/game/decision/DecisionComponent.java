package moster.game.decision;

import moster.app.SimulatorContext;
import moster.app.config.TestFlowConfig;
import moster.game.player.PlayerComponent;
import moster.infras.core.context.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * 玩家的决策组件。
 *
 * @author zhangfei
 */
public class DecisionComponent extends PlayerComponent {

    private static final Logger logger = LoggerFactory.getLogger("PLAYER");

    /**
     * 用于随机选择决策。
     */
    private static final Random random = new Random();

    /**
     * 当前处于的决策阶段。
     * 不同阶段，执行的决策来源是不一样的。
     */
    private Phase phase;

    /**
     * 玩家拥有的准备决策。
     */
    private ActionDecision[] prepareDecisions;

    /**
     * 玩家拥有的清理决策。
     */
    private ActionDecision[] cleanDecisions;

    /**
     * 玩家拥有的负荷决策，key为权重，值为对应的决策。
     */
    private TreeMap<Integer, ActionDecision> workloadDecisions;

    /**
     * 当前已经执行的负荷动作决策的次数。
     */
    private int workloadTimes;

    /**
     * 允许执行负荷动作决策的最大次数。
     */
    private int maxWorkloadTimes;

    /**
     * 所有决策的累计权重。
     */
    private int decisionWeightTotal;

    /**
     * 当前正在使用的决策。
     */
    ActionDecision currDecision;

    /**
     * 上一个被使用的决策。
     */
    ActionDecision lastDecision;

    @Override
    @SuppressWarnings("unchecked")
    public boolean load(Object ctx) {
        String workflowName = ((Map<String, String>) ctx).get("workflowName");
        TestFlowConfig.Workflow workflow = SimulatorContext.getGlobalConfig().getWorkflow(workflowName);
        this.maxWorkloadTimes = workflow.getWorkloadTimes();

        boolean prepareResult = initPrepareDecisions(workflow.getPrepare());
        boolean workloadResult = initWorkloadDecisions(workflow.getWorkloadWeight());
        boolean cleanResult = initCleanDecisions(workflow.getClean());

        if ((!prepareResult) || (!workloadResult) || (!cleanResult)) {
            return false;
        }

        this.phase = Phase.PREPARE;
        this.currDecision = this.prepareDecisions[0];
        return true;
    }

    /**
     * 初始化准备阶段的决策。
     *
     * @param prepareInfo 准备阶段的决策信息
     * @return 如果初始化成功就返回true，否则返回false
     */
    private boolean initPrepareDecisions(String[] prepareInfo) {
        ArrayList<ActionDecision> decisionList = new ArrayList<>(prepareInfo.length);

        for (String str : prepareInfo) {
            String decisionName = str.trim() + "Decision";
            ActionDecision decision = ServerContext.getBeanIfPresent(decisionName);
            if (decision == null) {
                logger.error("未找到Decision[{}]", decisionName);
                return false;
            }

            decision.setOwner(super.owner);
            decisionList.add(decision);
        }

        this.prepareDecisions = decisionList.toArray(new ActionDecision[0]);
        return true;
    }

    /**
     * 初始化负荷阶段的决策。
     *
     * @param workloadWeight 负荷阶段的权重信息
     * @return 如果初始化成功就返回true，否则返回false
     */
    private boolean initWorkloadDecisions(Map<String, Integer> workloadWeight) {
        int weightTotal = 0;
        TreeMap<Integer, ActionDecision> decisionMap = new TreeMap<>();

        for (Map.Entry<String, Integer> entry : workloadWeight.entrySet()) {
            String decisionName = entry.getKey() + "Decision";
            ActionDecision decision = ServerContext.getBeanIfPresent(decisionName);
            if (decision == null) {
                logger.error("未找到Decision[{}]", decisionName);
                return false;
            } else {
                decision.setOwner(super.owner);
            }

            // 将权重转换为命中决策的最大概率值，方便与随机值比较。
            // 比如有这样的三个权重[30, 30, 40]，转换为最大概率值为[30, 60, 100]。
            // 这样随机数就只需要与最大概率值进行比较。
            weightTotal += entry.getValue();
            decisionMap.put(weightTotal, decision);
        }

        this.workloadDecisions = decisionMap;
        this.decisionWeightTotal = weightTotal;
        return true;
    }

    /**
     * 初始化清理阶段的决策。
     *
     * @param cleanInfo 清理阶段的决策信息
     * @return 如果初始化成功就返回true，否则返回false
     */
    private boolean initCleanDecisions(String[] cleanInfo) {
        ArrayList<ActionDecision> decisionList = new ArrayList<>(cleanInfo.length);

        for (String str : cleanInfo) {
            String decisionName = str.trim() + "Decision";
            ActionDecision decision = ServerContext.getBeanIfPresent(decisionName);
            if (decision == null) {
                logger.error("未找到Decision[{}]", decisionName);
                return false;
            }

            decision.setOwner(super.owner);
            decisionList.add(decision);
        }

        this.cleanDecisions = decisionList.toArray(new ActionDecision[0]);
        return true;
    }

    /**
     * 切换到下一个决策。
     */
    public void switchNext() {
        switch(this.phase) {
            case PREPARE:
                switchPrepareDecision();
                break;

            case WORKLOAD:
                switchWorkloadDecision();
                break;

            case CLEAN:
            default:
                switchCleanDecision();
                break;
        }
    }

    /**
     * 切换到下一个清理阶段的决策。
     */
    private void switchCleanDecision() {
        int size = this.cleanDecisions.length;
        int position = 0;

        // 计算出当前决策所在的位置。
        for (; position < size; position++) {
            ActionDecision decision = this.cleanDecisions[position];
            if (decision == this.currDecision) {
                break;
            }
        }

        if (position != size - 1) {
            // 当前决策并不是清理阶段的最后一个决策，需要继续切换。
            switchTo(this.prepareDecisions[position + 1]);
        } else {
            logger.info("玩家[{}]执行完成所有决策", super.owner);
        }
    }

    /**
     * 切换到下一个准备阶段的决策。
     * 准备阶段的决策都是按顺序执行的。
     * 当所有的准备决策都执行完毕后，需要切换到逻辑阶段。
     */
    private void switchPrepareDecision() {
        int size = this.prepareDecisions.length;
        int position = 0;

        // 计算出当前决策所在的位置。
        for (; position < size; position++) {
            ActionDecision decision = this.prepareDecisions[position];
            if (decision == this.currDecision) {
                break;
            }
        }

        if (position == size - 1) {
            // 如果当前决策是准备阶段的最后一个决策，就需要切换到logic阶段。
            this.phase = Phase.WORKLOAD;
            ActionDecision firstDecision = this.workloadDecisions.values().stream().findFirst().get();
            switchTo(firstDecision);
        } else {
            // 继续切换到下一个准备阶段的决策。
            switchTo(this.prepareDecisions[position + 1]);
        }
    }

    /**
     * 切换到下一个负荷动作阶段的决策。
     */
    private void switchWorkloadDecision() {
        if (++this.workloadTimes >= this.maxWorkloadTimes) {
            // 已经执行到最大次数了，需要切换到清理阶段。
            this.phase = Phase.CLEAN;
            switchTo(cleanDecisions[0]);
            return;
        }

        // 1. 如果只有1个行为决策，那就只能继续执行同一个行为决策了。
        if (this.workloadDecisions.size() == 1) {
            this.workloadTimes++;
            ActionDecision firstDecision = this.workloadDecisions.values().stream().findFirst().get();
            switchTo(firstDecision);
            return;
        }

        int loopCount = 0;
        ActionDecision nextDecision = null;

        // 2. 如果有多个行为决策，尽量从其中选出一个不同于上一个的行为决策。
        do {
            // 如果随机数在某个决策的权重范围内，那么这个决策就被选中了。
            int num = DecisionComponent.random.nextInt(this.decisionWeightTotal) + 1;
            for (Map.Entry<Integer, ActionDecision> entry : this.workloadDecisions.entrySet()) {
                if (num <= entry.getKey()) {
                    nextDecision = entry.getValue();
                    break;
                }
            }

            // 如果随机了很多次，还是没得出合适的(可能是因为概率太低)，就用第一个决策。
            if (++loopCount >= 10) {
                nextDecision = this.workloadDecisions.values().stream().findFirst().get();
                break;
            }

            //在选择决策时，不让选择上一个使用的决策
        } while ((nextDecision == null) || (nextDecision == this.currDecision));

        this.workloadTimes++;
        switchTo(nextDecision);
    }

    /**
     * 切换到下个决策。
     */
    private void switchTo(ActionDecision nextDecision) {
        this.lastDecision = this.currDecision;
        this.lastDecision.onExit();

        this.currDecision = nextDecision;
        this.currDecision.onEnter();

        logger.info("玩家[{}]从[{}]决策切换到[{}]决策", super.owner,
                this.lastDecision.getClass().getSimpleName(), this.currDecision.getClass().getSimpleName());
    }

    public void setCurrDecision(ActionDecision currDecision) {
        this.currDecision = currDecision;
    }


    /**
     * 决策阶段。
     */
    static enum Phase {

        /**
         * 准备中。
         */
        PREPARE,

        /**
         * 正在执行负荷逻辑。
         */
        WORKLOAD,

        /**
         * 正在执行清理。
         */
        CLEAN,
    }

}
