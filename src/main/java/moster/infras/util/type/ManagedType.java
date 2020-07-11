package moster.infras.util.type;

import java.lang.annotation.*;

/**
 * <pre>
 * 用于标注某个类型是被管理的类型。
 * 当类型A被这个注解标注后，可以通过类型注册表获取所有这个类型A的Class。
 *
 * 关于继承：
 * 如果将这个注解标注在接口、类上，它们的子类、子接口也会拥有这个注解，也就是拥有这个注解的效果。
 *
 * 类型管理适合在某些场景下使用。
 * 比如有这么一个场景：
 * 技能的目标选择器会有多种，技能在配置的时候只会配置一个选择器类型和一些选择器参数；
 * 在注册所有的选择器时，直接根据选择器的抽象类型来获取所有具体的选择器类型；
 * 我们希望在添加新的目标选择器时，不需要添加或修改已有的选择器注册代码。
 * </pre>
 *
 * @author ranger2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ManagedType {

}
