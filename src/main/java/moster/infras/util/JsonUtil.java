package moster.infras.util;

import com.google.gson.Gson;

/**
 * @author zhangfei
 */
public abstract class JsonUtil {

    private static ThreadLocal<Gson> threadLocal = ThreadLocal.withInitial(() -> {
        Gson gson = new Gson();
        return gson;
    });

    /**
     * 将一个对象转换成json字符串。
     *
     * @param obj 对象
     * @return 对应的json字符串
     */
    public static String toJson(Object obj) {
        Gson gson = JsonUtil.threadLocal.get();
        return gson.toJson(obj);
    }

    /**
     * 将一个json字符串转换成对象。
     *
     * @param jsonStr json字符串
     * @param clazz 对象类型
     * @return 对象实例
     */
    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        Gson gson = JsonUtil.threadLocal.get();
        return gson.fromJson(jsonStr, clazz);
    }

    private JsonUtil() {

    }

}
