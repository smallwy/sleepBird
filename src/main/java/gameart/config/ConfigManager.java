package gameart.config;


import gameart.manager.TypeManager;
import gameart.config.utils.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.*;
import java.security.InvalidParameterException;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;


@Component
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

    private final static ConfigManager CONFIG_MANAGER = new ConfigManager();

    public static ConfigManager getInstance() {
        return CONFIG_MANAGER;
    }

    /**
     * 用于监控目录变化的服务。
     */
    private static WatchService watchService;

    /**
     * 监控目录的路径。
     */
    private static Path watchPath;

    /**
     * 被修改的字典文件，key为被修改的字典文件路径，value为重载倒计时。
     */
    private final Map<String, Integer> modifiedFiles = new HashMap<>();


    /**
     * 启动服务器的时候加载
     *
     * @throws Exception
     */
    public void init() throws Exception {
        this.simpleConfigClassMap = new HashMap<>();
        this.initCheck();

        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchPath = Paths.get("C:\\project\\src\\main\\resources\\config");
        this.watchPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);

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
        if (!simpleConfigClassMap.containsKey(jsonName)) {
            System.out.println("没有对应的数据表");
            return "加载失败";
        }
        Class aClass = simpleConfigClassMap.get(jsonName);
	/*	if (CommonConfig.class.isAssignableFrom(aClass)) {
			this.loadCommonTable((CommonConfig) aClass.newInstance());
		} else {*/
        this.loadSimpleTable((SimpleConfig) aClass.newInstance());
        /*}*/
        Set<IConfig> iConfigs = customConfig.get(aClass.getSimpleName());
        if (iConfigs != null) {
            for (IConfig config : iConfigs) {
                config.loadConf();
            }
        }
        System.out.println(jsonName + "加载成功");
        return jsonName;
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

    /**
     * 检查有哪些字典文件被修改了。
     */
    private void checkModifiedFiles() {
        WatchKey key = this.watchService.poll();
        if (key == null) {
            return;
        }

        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            if (kind == StandardWatchEventKinds.OVERFLOW) {
                continue;
            }

            @SuppressWarnings("unchecked")
            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
            Path changed = pathEvent.context();
            Path absolute = this.watchPath.resolve(changed);

            File file = absolute.toFile();
            String path = file.getPath();
            this.modifiedFiles.put(path, 10);
        }

        key.reset();
    }

    /**
     * 检查是否有字典文件被修改。
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void checkModifiedDict() {
        tick();
    }

    private void tick() {
        checkModifiedFiles();
        reloadModifiedFiles();
    }

    /**
     * 重载被修改的字典文件。
     */
    private void reloadModifiedFiles() {
        if (this.modifiedFiles.size() <= 0) {
            return;
        }

        // 减少所有待重载的数据字典文件的倒计时。
        this.modifiedFiles.forEach((String path, Integer countdown) -> this.modifiedFiles.put(path, countdown - 1));

        List<String> dictList = new ArrayList<>();
        Iterator<Map.Entry<String, Integer>> iterator = this.modifiedFiles.entrySet().iterator();
        while (iterator.hasNext()) {
            // 当倒计时为0时，对应被修改文件的字典会进行重载。
            Map.Entry<String, Integer> entry = iterator.next();

            String path = entry.getKey();
            File file = new File(path);

            // 检查文件是否被占用，如果被占用就暂时不重载它对应的字典。
           /* if (!file.renameTo(file)) {
                this.modifiedFiles.put(path, 10);
                continue;
            }*/

            // 根据文件名找到对应的字典。
            String filename = file.getName();

            Iterator<Map.Entry<String, Class>> iterator1 = this.simpleConfigClassMap.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry<String, Class> entry1 = iterator1.next();
                String dict = entry1.getKey();
                if (filename.equals(dict + ".json")) {
                    dictList.add(dict);
                    break;

                }
            }
            iterator.remove();
        }

        // 对待重载的字典排序后再开始加载。
        for (String dict : dictList) {
            try {
                reload(dict);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
