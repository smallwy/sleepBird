package gameart.bean;


/** 
 * 表名字【item.xlsx】 
 */
public class item extends SimpleConfig {
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
}