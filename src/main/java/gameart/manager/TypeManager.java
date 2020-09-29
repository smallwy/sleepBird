 /*****************************
* Copyright 2018 360游戏艺术*
* **************************/
package gameart.manager;

 import gameart.annotation.TypeManagered;
 import org.springframework.context.ApplicationContext;

 import java.util.*;
 import java.util.Map.Entry;

 import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;


 public class TypeManager {

	private final static TypeManager typesMgr = new TypeManager();
	private final static Map<Class<?>, List<?>> types = new HashMap<Class<?>, List<?>>();
	private final static List<?> EMPTY = new ArrayList<>(0);

	public static TypeManager getInstance() {
		return typesMgr;
	}

	public void init(ApplicationContext context) {
		Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(TypeManagered.class);
		Set<Class<?>> typeSet = new HashSet<Class<?>>();
		for (Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
			List<Class<?>> interfaces = new LinkedList<>();
			getInterfaces(entry.getValue().getClass(), interfaces);
			for (Class<?> interf : interfaces) {
				if (interf.isAnnotationPresent(TypeManagered.class)) {
					typeSet.add(interf);
				}
			}
		}
		Map<String, Object> beans = context.getBeansWithAnnotation(TypeManagered.class);
		for (Object o : beans.values()) {
			for (Class<?> type : typeSet) {
				if (type.isAssignableFrom(o.getClass())) {
					add(o, type);
				}
			}
		}
		// for (Class<?> type : typeSet) {
		// for (Entry<String, ?> entry : context .getBeansOfType(type)
		// .entrySet()) {
		// add(entry.getValue(), entry.getValue().getClass());
		// }
		// }
		info();
	}

	private void getInterfaces(Class<? extends Object> clazz, List<Class<?>> interfaces) {
		if (clazz == Object.class) {
			return;
		}

		Class<?>[] newInterfaces = clazz.getInterfaces();
		if (newInterfaces.length != 0) {
			interfaces.addAll(Arrays.asList(newInterfaces));
		}
		interfaces.add(clazz.getSuperclass());
		getInterfaces(clazz.getSuperclass(), interfaces);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void add(Object value, Class<? extends Object> clazz) {
		List list = types.get(clazz);
		if (list == null) {
			list = new LinkedList<>();
			types.put(clazz, list);
		}
		if (!list.contains(value)) {
			list.add(value);
		}

		sortByIndex(list);
		for (Class<?> interf : clazz.getInterfaces()) {
			add(value, interf);
		}
	}

	/**
	 * 按照annotation TypeIndex 制定的顺序排序
	 * 
	 * @param list
	 */
	public static <T> void sortByIndex(List<T> list) {
		Collections.sort(list, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				TypeManagered index1 = o1.getClass().getAnnotation(TypeManagered.class);
				TypeManagered index2 = o2.getClass().getAnnotation(TypeManagered.class);
				return index1.index() < index2.index() ? -1 : 1;
			}
		});
	}

	private void info() {
		log("");
		log("#######################################################");
		log("# Types has found as following infomation:");
		log("#######################################################");
		Set<Entry<Class<?>, List<?>>> entrySet = types.entrySet();
		for (Entry<Class<?>, List<?>> entry : entrySet) {
			log("#-----" + entry.getKey().getName());
			List<?> value = entry.getValue();
			for (Object o : value) {
				log("#   |-----index=" + o.getClass().getAnnotation(TypeManagered.class).index() + ", class="
						+ o.getClass().getName());
			}
		}
		log("#######################################################");
		log("");
	}

	/**
	 * @param
	 */
	private void log(String msg) {
		LOGGER.info(msg);
	}

	/**
	 * 获取某种类型的监听器
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getTypes(Class<T> clazz) {
		List<T> list = (List<T>) types.get(clazz);
		if (list == null) {
			return (List<T>) EMPTY;
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public List<Object> getMultiTypes(Class... clazzs) {
		List<Object> list = new ArrayList<>();
		for (Class clazz : clazzs) {
			List l = types.get(clazz);
			if (l == null) {
				l = new ArrayList<>(0);
				types.put(clazz, l);
			}
			if (l.size() > 0) {
				list.add(l);
			}
		}
		return list;
	}

	/*public <T, A> void operateWith(Class<T> clazz, ITypeOperate<T, A> p, A argType) {
		List<T> list = getTypes(clazz);
		if (list != null) {
			for (T t : list) {
				try {
					p.operate(t, argType);
				} catch (Exception e) {
					LOGGER.error(Strings.format("%0", argType == null ? "null" : argType.toString()), e);
				}
			}
		}
	}*/
}
