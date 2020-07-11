package moster.infras.util.type;

import org.springframework.beans.BeansException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * <pre>
 * 用于获取被管理类型的扫描器。
 * 通过扫描指定的包，来建立被查找类型的映射关系。
 * 比如通过查找类型A，来获取所有是类型A的类型。
 *
 * 比如有这样的层次结构：A extends B implements C。
 * 如果C被注解@ManagedType修饰，那么扫描后就会建立如下的映射关系：
 * A -> [A, B, C]
 * B -> [B, C]
 * C -> [C]
 * </pre>
 *
 * @author ranger2
 */
public class ManagedTypeScanner {

  private static final String RESOURCE_PATTERN = "**/*.class";

  private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

  private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
      resourcePatternResolver);

  /**
   * 将要扫描的包路径。
   */
  private String[] scanPackages;

  /*
   * 进行扫描，获取所有被管理类型的映射关系。
   */
  public void scan()
      throws BeansException {
    for (String scanPackage : this.scanPackages) {
      String packagePath = scanPackage.replace('.', '/');
      String resourcePath =
          ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + packagePath + "/" + RESOURCE_PATTERN;
      scanTypes(resourcePath);
    }
  }

  /**
   * 扫描路径上的被管理类型。
   *
   * @param resourcePath 路径
   */
  private void scanTypes(String resourcePath) {
    // 获取这个路径上的所有资源信息。
    Resource[] resources;
    try {
      resources = this.resourcePatternResolver.getResources(resourcePath);
    } catch (IOException e) {
      throw new RuntimeException("扫描路径[" + resourcePath + "]上的类型时出现错误", e);
    }

    HashMap<String, String> results = new HashMap<>();
    Stack<String> stack = new Stack<>();

    for (Resource resource : resources) {
      results.clear();
      stack.clear();

      try {
        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
        String className = metadataReader.getClassMetadata().getClassName();
        search(className, stack, results);
        TypeRegistry.registerTypes(results);
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("扫描类型时出现错误", e);
      }
    }
  }

  /**
   * 在这个类型和继承结构上进行查找，判断它是否是被管理的类型。
   *
   * @param className 被查找类型的fqn
   * @param stack 存放被查找类型fqn的栈，这里面存放都是被查找类型的层次结构上的类型
   * @param results key为查找类型fqn，value为被查找类型fqn
   */
  private void search(String className, Stack<String> stack, Map<String, String> results)
      throws IOException {
    stack.push(className);

    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(className);
    ClassMetadata classMetadata = metadataReader.getClassMetadata();

    // 查找父类，看它是不是被管理的类型。
    String superClassName = classMetadata.getSuperClassName();
    if ((superClassName != null) && !Object.class.getName().equals(superClassName)) {
      search(superClassName, stack, results);
    }

    // 查找父接口，看它是不是被管理的类型。
    String[] interfaceNames = classMetadata.getInterfaceNames();
    for (String interfaceName : interfaceNames) {
      search(interfaceName, stack, results);
    }

    AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
    if (annotationMetadata.hasAnnotation(ManagedType.class.getName())) {
      // 栈的最底端是子类型，栈中的其它元素都是子类型的父类(或父接口)。
      // 建立父类(或父接口)与子类型的查找关系。
      // 也就是遍历这个子类型的层次结构，将层次结构上的所有类型都与子类型建立映射。
      String finalClassName = stack.firstElement(); //最先添加的元素，也就是继承关系的层次结构中最上层的元素
      stack.forEach((element) -> results.put(element, finalClassName));
      // 假如有这样的继承关系：C extends B implements A
      // 那么results中就会添加这样的映射关系：
      // A -> A
      // A -> B
      // A -> C
    }

    stack.pop();
  }

  public static void main(String[] args) {
    Stack<Integer> stack = new Stack<>();
    stack.push(1);
    stack.push(2);
    stack.push(3);
    System.out.println(stack.firstElement());
  }
  public ManagedTypeScanner(String[] scanPackages) {
    this.scanPackages = scanPackages;
  }

}
