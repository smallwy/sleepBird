package rookiex.ai;


import rookiex.aiTree.AIContext;

public class AIIsBirthday extends AICondition{

	@Override
	public boolean is(AIContext context) {
		
		if(context.getProperty("birthday") == null){
			return false;
		}
		
		if(context.getProperty("birthday").equals("today")){
			return true;
		}else {
			return false;
		}
	}

}
