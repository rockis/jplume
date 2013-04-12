package jplume.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.converters.Converter;
import jplume.core.URLPatternException;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.View;

public class ViewMethod {

	private Logger logger = LoggerFactory.getLogger(ViewMethod.class);

	private final Method method;
	
	/**
	 * key: index of pathvar
	 */
	private final TreeMap<Integer, Argument> arguments;
	
	private final String[] requireMethods;

	private ViewMethod(Method method, TreeMap<Integer, Argument> arguments, 
			            String[] requireMethods) {
		this.method = method;
		this.arguments = arguments;
		this.requireMethods = requireMethods;
	}

	public static ViewMethod create(Method method) throws URLPatternException {
		Class<?>[] argumentTypes = method.getParameterTypes();

		TreeMap<Integer, Argument> arguments = new TreeMap<Integer, Argument>();

		int argIndex = 0;
		int pathIndex = 0;
		for (Annotation[] annotations : method.getParameterAnnotations()) {
			if (annotations.length > 1) {
				throw new URLPatternException("View method's argument has one annotation as most");
			}else if(annotations.length == 1) {
				Annotation annotation = annotations[0];
				if (annotation instanceof PathVar) {
					PathVar anno = (PathVar) annotation;
					int pi = pathIndex;
					if (anno.index() > 0) {
						pi = anno.index() - 1;
					}
					arguments.put(argIndex, new PathArgument(
							argumentTypes[argIndex], pi));
					pathIndex++;
					
				}else if (annotation instanceof QueryVar) {
					QueryVar anno = (QueryVar) annotation;
					String name = anno.name();
					String defval = anno.defval();
					arguments.put(argIndex, new QueryArgument(argumentTypes[argIndex], name, defval));
				}else {
					throw new URLPatternException("Annotation of view method's argument must be PathVar or QueryVar");
				}
			}else{
				throw new URLPatternException("Invalid Argument:" + argIndex + " of method " + method.getDeclaringClass() + ":" + method.getName());
			}
			argIndex++;
		}

		String[] requireMethods = new String[0];
		View anno = method.getAnnotation(View.class);
		if (anno != null) {
			requireMethods = anno.methods();
		}
		return new ViewMethod(method, arguments, requireMethods);
	}

	public boolean match(String[] pathVars) {
		for (Argument argument : arguments.values()) {
			if (argument instanceof PathArgument) {
				int pathIndex = ((PathArgument)argument).pathIndex;
				String var = pathVars[pathIndex];
				if (!Converter.isValid(argument.type, var)) {
					return false;
				}
			}
		}
		return true;
	}

	public Response handle(Request request, String... pathVars) {
		try {
			if (requireMethods.length > 0
					&& Arrays.binarySearch(requireMethods, request.getMethod().toLowerCase()) < 0) {
				return HttpResponse.forbidden();
			}
			Class<?> actionClass = this.method.getDeclaringClass();
			Object action = actionClass.newInstance();
			
			List<Object> arguments = new ArrayList<Object>();
			
			for (Argument argument : this.arguments.values()) {
				arguments.add(argument.get(request, pathVars));
			}

			Response resp = null;
			Class<?> returnType = this.method.getReturnType();
			if (returnType.equals(String.class)) {
				resp = HttpResponse.ok((String) this.method.invoke(action,
						arguments.toArray(new Object[0])));
			} else if (Response.class.isAssignableFrom(returnType)) {
				resp = (Response) this.method.invoke(action,
						arguments.toArray(new Object[0]));
			}
			return resp;
		} catch (Exception e) {
			Throwable except = e;
			while(except.getCause() != null) {
				except = except.getCause();
			}
			throw new ViewException(except);
		}
	}

	private static abstract class Argument {
		
		protected Class<?> type;
		public Argument(Class<?> type) {
			this.type = type;
		}
		abstract Object get(Request request, String... pathVars);
		
	}
	
	private static class PathArgument extends Argument{
		private int pathIndex;

		public PathArgument(Class<?> type, int pathIndex) {
			super(type);
			this.pathIndex = pathIndex;
		}

		@Override
		Object get(Request request, String... pathVars) {
			return Converter.convert(this.type, pathVars[pathIndex]);
		}
	}
	
	private static class QueryArgument extends Argument {
		private String name;
		private String defval;

		public QueryArgument(Class<?> type, String name, String defval) {
			super(type);
			this.name   = name;
			this.defval = defval;
		}
		
		@Override
		Object get(Request request, String... pathVars) {
			String val = request.getParam(name);
			if (val == null || !Converter.isValid(this.type, val)) {
				return Converter.convert(this.type, defval);
			}
			return Converter.convert(this.type, val);
		}
	}
}