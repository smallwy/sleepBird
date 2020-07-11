package moster.infras.util.type;

import java.util.*;

/**
 * 类型注册表。
 *
 * @author ranger2
 */
public class TypeRegistry {

  /**
   * 所有被管理的类型，key为查找类型，value为包含对应类型的列表。
   */
  private static final Map<Class<?>, List<Class<?>>> typesMap = new HashMap<>();

  /**
   * 所有被管理类型的映射关系，key为父类型，value为包含所有子类型的列表。
   * 通过这个映射关系，可以根据父类型找到所有子类型。
   */
  private static final Map<Class<?>, List<Class<?>>> subTypesMap = new HashMap<>();

  /**
   * 初始化。
   *
   * @param scanPackages 指定要扫描的包路径
   */
  public static void init(String... scanPackages) {
    ManagedTypeScanner scanner = new ManagedTypeScanner(scanPackages);
    scanner.scan();
  }

  /**
   * 获取所有的子类型。
   *
   * @param parentClass 父类型
   * @return 包含子类型的所有Class列表
   */
  @SuppressWarnings("unchecked")
  public static <T, C extends Class<? extends T>> List<C> getSubTypes(Class<T> parentClass) {
    List<Class<?>> childClassList = TypeRegistry.typesMap.get(parentClass);
    if (childClassList != null) {
      return (List<C>) childClassList;
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * <pre>
   * 注册新的类型查找关系。
   * 比如想通过Runnable.class查找到Thread.class，
   * 就必须要注册它们之间的对应关系：Runnable.class -> Thread.class
   * </pre>
   *
   * @param classNames 包含类型映射关系的map，key为查找类型，value为对应结果类型
   * @throws ClassNotFoundException 如果没有这些类型的Class就会抛出此异常
   */
  public static void registerTypes(Map<String, String> classNames) throws ClassNotFoundException {
    for (Map.Entry<String, String> entry : classNames.entrySet()) {
      Class<?> keyClass = Class.forName(entry.getKey());
      Class<?> valueClass = Class.forName(entry.getValue());

      // 只允许注册子类型，不允许注册相同类型。
      // 也就是，只能通过父类查找子类，不能通过相同类型查找相同类型(没有意义)。
      if (keyClass == valueClass) {
        continue;
      }

      List<Class<?>> list = typesMap.computeIfAbsent(keyClass, k -> new ArrayList<>(1));
      if (!list.contains(valueClass)) {
        list.add(valueClass);
      }
    }
  }

}
