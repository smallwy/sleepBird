package rookiex.ai;


import rookiex.aiTree.AIContext;
import rookiex.aiTree.Node;
import rookiex.constant.Contsant;

import java.util.List;

public class AISelector implements Node {

	/** 选择节点执行逻辑 **/
	@Override
	public int excute(AIContext context) {
		int size = sonNode.size();
		for (int i = 0; i < size; i++) {
			int ecResult = sonNode.get(i).excute(context);
			// 如果子节点返回正确或者运行中,向父节点返回正确或运行中
			if (ecResult == Contsant.IS_TRUE || ecResult == Contsant.IS_RUN) {
				return ecResult;
			}
		}
		// 如果全部返回失败,向父节点返回失败
		return Contsant.IS_FALSE;
	}

	public List<Node> getSon() {
		return sonNode;
	}

	public void addSon(Node son) {
		sonNode.add(son);
	}
}