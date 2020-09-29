package gameart.bean;
import gameart.config.utils.SimpleConfig;
import java.util.*;


/** 
 * 表名字【good.xlsx】 
 */
public class good extends SimpleConfig {
	//id
	private int id;
	//名字
	private String name;
	//大类
	private int type;
	//小类
	private int subType;
	//行为
	private int action;
	//效果Id
	@Config(parser = "toIntArray")
	private int[] effectIds;
	//各阶段出现的角色数量和每个角色出现的权重
	@Config(parser = "str2IntArrayList")
	private List<int[]> stageNumWeight;

	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public int getType(){
		return type;
	}
	public void setType(int type){
		this.type = type;
	}
	public int getSubType(){
		return subType;
	}
	public void setSubType(int subType){
		this.subType = subType;
	}
	public int getAction(){
		return action;
	}
	public void setAction(int action){
		this.action = action;
	}
	public int[] getEffectIds(){
		return effectIds;
	}
	public List<int[]> getStageNumWeight(){
		return stageNumWeight;
	}

	//====================== 自定义解析 =====================//
	private int[] toIntArray(String data){
		 return ConfParseUtils.toIntArray(data);
	}
	private List<int[]> str2IntArrayList(String data){
		 return ConfParseUtils.str2IntArrayList(data);
	}
}