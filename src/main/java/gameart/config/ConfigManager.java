package gameart.config;


import gameart.manager.TypeManager;
import gameart.config.utils.*;

import java.security.InvalidParameterException;
import java.util.*;


public class ConfigManager {
	/**
	 * 需要加载的配表的class
	 */
	private static Map<String, Class> simpleConfigClassMap = new HashMap<>();
	/**
	 * 自定义解析的加载class
	 */
	private static Map<String, Set<IConfig>> customConfig = new HashMap<>();
	/**
	 * 加载数据后的检测
	 */
	private static Map<Set<String>, AbstractConfigCheck> checkMap = new HashMap<>();

	//配置数据
	private static Map<String, Map<Integer, Object>> configMap = new HashMap<>();

	private final static    ConfigManager CONFIG_MANAGER= new ConfigManager();

	public static ConfigManager getInstance(){
		return CONFIG_MANAGER;
	}

	/**
	 * 启动服务器的时候加载
	 *
	 * @throws Exception
	 */
	public void init() throws Exception {
		this.simpleConfigClassMap = new HashMap<>();
		this.initCheck();


		//单个表
		List<SimpleConfig> simpleTypes = TypeManager.getInstance().getTypes(SimpleConfig.class);
		for (SimpleConfig config : simpleTypes) {
			if (config.getClass() == SimpleConfig.class)
				continue;
			String simpleName = config.getClass().getSimpleName();
			if (simpleConfigClassMap.containsKey(simpleName)) {
				throw new Exception("重复的数据解释对象:" + simpleName);
			}
			simpleConfigClassMap.put(simpleName, config.getClass());
			this.loadSimpleTable(config);

			//检测开始
			this.check(config);
		}

		List<IConfig> configsTypes = TypeManager.getInstance().getTypes(IConfig.class);
		for (IConfig config : configsTypes) {
			Class<?>[] value = config.getClass().getAnnotation(Config.class).value();
			if (value == null || value.length <= 0) {
				throw new InvalidParameterException("自定义解析，没有添加需要reload的对象，请检查!");
			}
			for (Class clazz : value) {
				String simpleName = clazz.getSimpleName();
				Set<IConfig> configList = customConfig.get(simpleName);
				if (configList == null) {
					configList = new HashSet<>();
					customConfig.put(simpleName, configList);
				}
				configList.add(config);
			}
			config.loadConf();
		}
	}


	private void check(SimpleConfig config) {
		String simpleName = config.getClass().getSimpleName();
		for (Map.Entry<Set<String>, AbstractConfigCheck> entry : checkMap.entrySet()) {
			Set<String> key = entry.getKey();
			if (key.contains(simpleName) && configMap.keySet().containsAll(key)) {
				entry.getValue().check();
			}
		}
	}

	private void initCheck() {
		List<AbstractConfigCheck> checkTypes = TypeManager.getInstance().getTypes(AbstractConfigCheck.class);
		for (AbstractConfigCheck check : checkTypes) {
			ConfigCheck annotation = check.getClass().getAnnotation(ConfigCheck.class);
			Class<?>[] value = annotation.value();
			if (value == null || value.length <= 0)
				throw new RuntimeException("配置检测数据ConfigCheck注解没有配置class");
			Set<String> classNameSet = new HashSet<>();
			for (Class<?> clazz : value) {
				classNameSet.add(clazz.getSimpleName());
			}
			checkMap.put(classNameSet, check);
		}
	}


	/**
	 * 数据加载
	 *
	 * @param jsonName
	 * @return
	 * @throws Exception
	 */
	private String reload(String jsonName) throws Exception {
		if (simpleConfigClassMap.containsKey(jsonName)) {
			return "没有对应的数据表";
		}
		Class aClass = simpleConfigClassMap.get(jsonName);
	/*	if (CommonConfig.class.isAssignableFrom(aClass)) {
			this.loadCommonTable((CommonConfig) aClass.newInstance());
		} else {*/
			this.loadSimpleTable((SimpleConfig) aClass.newInstance());
		/*}*/
		Set<IConfig> iConfigs = customConfig.get(aClass.getSimpleName());
		if (iConfigs != null) {
			for(IConfig config : iConfigs){
				config.loadConf();
			}
		}
		return "加载成功";
	}

/*	private boolean loadCommonTable(CommonConfig config) throws Exception {
		config.setValue();
		return true;
	}*/

	private boolean loadSimpleTable(SimpleConfig config) throws Exception {
		Map<Integer, Object> tableConfigMap = config.loadValue();
		addConfigData(config.getClass(), tableConfigMap);
		return true;
	}

	private void addConfigData(Class clazz, Map<Integer, Object> tableConfigMap) {
		configMap.put(clazz.getSimpleName(), tableConfigMap);
		simpleConfigClassMap.put(clazz.getSimpleName(), clazz);
	}

	public <C> void addConfigData(Class clazz, C config, int id) {
		Map<Integer, Object> configs = configMap.get(clazz.getSimpleName());
		if (configs == null) {
			configs = new LinkedHashMap<>();
			configMap.put(clazz.getSimpleName(), configs);
		}
		configs.put(id, config);
	}

	public static <C> C getConfig(Class<C> clazz, int id) {
		Map<Integer, Object> configs = configMap.get(clazz.getSimpleName());
		if (configs != null) {
			return (C) configs.get(id);
		}
		return null;
	}

	public static <C> Map<Integer, C> getConfig(Class<C> clazz) {
		Map<Integer, Object> configs = configMap.get(clazz.getSimpleName());
		if (configs != null) {
			return (Map<Integer, C>) configs;
		}
		return null;
	}
}
