package moster.infras.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Map;

/**
 * @author zhangfei
 */
public abstract class YamlUtils {

    /**
     * 将yaml配置文件转换成某个类型的对象。
     *
     * @param typeClass 对象类型
     * @param configLocation yaml配置文件相对于classpath的所在位置
     * @return 具有yaml配置数据的对象
     */
    public static <T> T load(Class<T> typeClass, String configLocation) {
        InputStream inputStream = typeClass.getClassLoader().getResourceAsStream(configLocation);
        return new Yaml(new Constructor((typeClass))).load(inputStream);
    }

    public static Map<String, Object> load(String configLocation) {
        InputStream inputStream = YamlUtils.class.getClassLoader().getResourceAsStream(configLocation);
        return new Yaml().load(inputStream);
    }

    private YamlUtils() {

    }

}
