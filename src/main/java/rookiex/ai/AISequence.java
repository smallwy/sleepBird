package rookiex.ai;


import rookiex.aiTree.AIContext;
import rookiex.aiTree.Node;
import rookiex.constant.Contsant;

import java.util.List;

public class AISequence implements Node {

	
	public List<Node> getSon() {
		return sonNode;
	}

	public void addSon(Node son) {
		sonNode.add(son);
	}
	
	/**顺序节点执行逻辑**/
	@Override
	public int excute(AIContext context){
		int size = sonNode.size();
		for (int i = 0; i < size; i++) {
			int ecResult = sonNode.get(i).excute(context);
			//如果子节点返回错误或者运行中,向父节点返回错误或运行中
			if (ecResult == Contsant.IS_FALSE || ecResult == Contsant.IS_RUN) {
				return ecResult;
			}
		}
		//如果全部返回正确,向父节点返回正确
		return Contsant.IS_TRUE;
	}

}