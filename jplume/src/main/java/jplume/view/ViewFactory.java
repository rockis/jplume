package jplume.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import jplume.view.annotations.PathVar;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.View;

import jplume.view.ArgumentBuilder.PathNamedArgument;
import jplume.view.ArgumentBuilder.PathIndexedArgument;
import jplume.view.ArgumentBuilder.QueryArgument;

public class ViewFactory {

	public static ViewMethod createView(Pattern pattern, Method method)
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
				} else {
					throw new ViewException(
							"Annotation of view method's argument must be PathVar or QueryVar");
				}
			} else {
				throw new ViewException("Invalid Argument:" + argIndex
						+ " of method " + method.getDeclaringClass() + ":"
						+ method.getName());
			}
			argIndex++;
		}

		String[] requireMethods = new String[0];
		View anno = method.getAnnotation(View.class);
		if (anno != null) {
			requireMethods = anno.methods();
		}

		return new ViewMethod(pattern, method, argBuilder, requireMethods);
	}
}
