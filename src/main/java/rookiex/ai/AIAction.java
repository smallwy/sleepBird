package rookiex.ai;


import rookiex.aiTree.AIContext;
import rookiex.aiTree.AILeaf;

public class AIAction extends AILeaf {
	
	@Override
	public int excute(AIContext context) {
		this.run(context);
		return 0;
	}
	public void run(AIContext context){
		System.out.println("hello ai");
	}
}
