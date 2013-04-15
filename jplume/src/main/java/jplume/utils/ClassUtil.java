package jplume.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jplume.conf.Settings;

public class ClassUtil {

	
	public static Class<?> forName(String className) throws ClassNotFoundException {
		if (className.charAt(0) == '.'){
			className = Settings.get("DEFAULT_PACKAGE_PREFIX") + className;
		}
		return Class.forName(className);
	}

	public static Object newInstance(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return forName(className).newInstance();
	}
	
	public static Method[] getMethods(String className, String methodName) throws ClassNotFoundException {
		List<Method> ms = new ArrayList<>();
		Class<?> clazz = forName(className);
		for(Method m : clazz.getMethods()) {
			if (m.getName().equals(methodName)) {
				ms.add(m);
			}
		}
		return ms.toArray(new Method[0]);
	}
	
	public static Method[] getMethods(String classMethodName) throws ClassNotFoundException {
		int lastIndexOfColon = classMethodName.lastIndexOf(':');
		if (lastIndexOfColon < 0) {
			throw new IllegalArgumentException("Invalid class method name");
		}
		String className = classMethodName.substring(0, lastIndexOfColon);
		String methodName = classMethodName.substring(lastIndexOfColon + 1);
		return getMethods(className, methodName);
	}
}
