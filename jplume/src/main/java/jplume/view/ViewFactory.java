package jplume.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jplume.conf.IllegalURLPattern;
import jplume.utils.ClassUtil;
import jplume.view.annotations.Form;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.ViewMethod;

import jplume.view.ArgumentBuilder.PathNamedArgument;
import jplume.view.ArgumentBuilder.PathIndexedArgument;
import jplume.view.ArgumentBuilder.QueryArgument;

public class ViewFactory {

	public static View createView(String classMethodName) {
		int indexOfLastColon = classMethodName.lastIndexOf(':');

		if (indexOfLastColon < 1) {
			throw new IllegalURLPattern("classname:methodname!alias");
		}
		String className = classMethodName.substring(0, indexOfLastColon);
		String methodName = classMethodName.substring(indexOfLastColon + 1);
		String methodAlias = null;
		int indexOfLastExcl = methodName.lastIndexOf('!');
		if (indexOfLastExcl > 0) {
			methodAlias = methodName.substring(indexOfLastExcl + 1);
			methodName = methodName.substring(0, indexOfLastExcl);
		}
		
		try {
			Method[] possibleMethods = ClassUtil.getMethods(className,
					methodName);

			if (possibleMethods.length == 1) {
				return createView(possibleMethods[0]);
			} else if (possibleMethods.length == 0) {
				throw new ViewException("No Such Method '" + className + ":" + methodName
						+ "'");
			} else {
				View view = null;
				for (Method m : possibleMethods) {
					ViewMethod anno = m.getAnnotation(ViewMethod.class);
					
					if ((anno == null && methodAlias == null)
							|| (anno != null && anno.alias()
									.equals(methodAlias))) {
						if (view != null) {
							throw new IllegalURLPattern("Ambiguous method:"
									+ m.getDeclaringClass() + "." + m.getName());
						}
						view = ViewFactory.createView(m);
					}
				}
				if (view == null) {
					throw new ViewException("No eplicit Method '" + className
							+ ":" + methodName + "'");
				}
				return view;
			}

		} catch (ClassNotFoundException e) {
			throw new ViewException("Class '" + className
					+ "' not found, "
					+ ", classmethod name is " + classMethodName);
		}
	}
	
	public static View createView(Method method)
			throws ViewException {
		Class<?>[] argumentTypes = method.getParameterTypes();

		ArgumentBuilder argBuilder = new ArgumentBuilder();
		int argIndex = 0;
		for (Annotation[] annotations : method.getParameterAnnotations()) {
			if (annotations.length > 1) {
				throw new ViewException(
						"View method's argument has one annotation as most");
			} else if (annotations.length == 1) {
				Annotation annotation = annotations[0];
				if (annotation instanceof PathVar) {
					PathVar anno = (PathVar) annotation;
					if (anno.name().length() == 0) {
						argBuilder.addArgument(new PathIndexedArgument(
								argumentTypes[argIndex], argIndex));
					} else {
						argBuilder.addArgument(new PathNamedArgument(
								argumentTypes[argIndex], anno.name()));
					}
				} else if (annotation instanceof QueryVar) {
					QueryVar anno = (QueryVar) annotation;
					String name = anno.name();
					String defval = anno.defval();
					argBuilder.addArgument(new QueryArgument(
							argumentTypes[argIndex], name, defval));
				} else if (annotation instanceof Form) {
					argBuilder.setHasFormArg(true);
				} else {
					throw new ViewException(
							"Annotation of view method's argument must be PathVar, QueryVar or Form");
				}
			} else {
				throw new ViewException("Invalid Argument:" + argIndex
						+ " of method " + method.getDeclaringClass() + ":"
						+ method.getName());
			}
			argIndex++;
		}

		String[] requireMethods = new String[0];
		ViewMethod anno = method.getAnnotation(ViewMethod.class);
		if (anno != null) {
			requireMethods = anno.methods();
		}

		return new View(method, argBuilder, requireMethods);
	}
}
