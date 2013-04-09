package jplume.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import jplume.converters.Converter;
import jplume.core.URLPatternException;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.RequireHttpMethod;

public class ViewMethod {
	
	private final Method method;
	private final TreeMap<Integer, Argument> arguments;
	private final boolean requireRequest;
	
	private final List<String> requireMethods;
	
	private ViewMethod(Method method, TreeMap<Integer, Argument> arguments, boolean requireRequest, List<String> requireMethods) {
		this.method = method;
		this.arguments = arguments;
		this.requireRequest = requireRequest;
		this.requireMethods = requireMethods;
	}
	
	public static ViewMethod create(Method method) throws URLPatternException {
		Class<?>[] argumentTypes = method.getParameterTypes();
		
		TreeMap<Integer, Argument> arguments = new TreeMap<Integer, Argument>();
		
		boolean requireRequest = false;
		int index = 0;
		for(Annotation[] annotations : method.getParameterAnnotations()){
			index++;
			for (Annotation annotation : annotations) {
				if (annotation instanceof PathVar) {
					PathVar anno = (PathVar)annotation;
					int argIndex = index;
					if (anno.index() > 0) {
						argIndex = anno.index();
					}
					arguments.put(argIndex, new Argument(index, argumentTypes[index - 1]));
					continue;
				}
			}
			if (index == argumentTypes.length) {
				if (Request.class.isAssignableFrom(argumentTypes[index - 1])){
					requireRequest = true;
				}
			}
		}
		
		List<String> requireMethods = new ArrayList<String>();
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof RequireHttpMethod) {
				requireMethods.add(((RequireHttpMethod)annotation).method().toLowerCase());
			}
		}
		return new ViewMethod(method, arguments, requireRequest, requireMethods);
	}
	
	public boolean match(String[] pathVars) {
		for(Argument argument : arguments.values()) {
			int index = argument.index;
			String var = pathVars[index - 1];
			if (!Converter.isValid(argument.type, var)) {
				return false;
			}
		}
		return true;
	}
	
	public Object newAction() {
		Class<?> actionClass = this.method.getDeclaringClass();
		try {
			return actionClass.newInstance();
		} catch (InstantiationException e) {
			throw new InvalidActionException("Couldn't create instance of action class:" + actionClass);
		} catch (IllegalAccessException e) {
			throw new InvalidActionException("Couldn't create instance of action class:" + actionClass);
		}
	}
	
	public Response handle(Request request, String... pathVars) {
		try {
			if (requireMethods.size() > 0 && requireMethods.indexOf(request.getMethod().toLowerCase()) < 0){
				return HttpResponse.forbidden();
			}
			Object action = newAction();
			List<Object> arguments = new ArrayList<Object>();
			for(Argument argument : this.arguments.values()) {
				int index = argument.index;
				arguments.add(Converter.convert(argument.type, pathVars[index - 1]));
			}
			
			if (requireRequest) {
				arguments.add(request);
			}
			Class<?> returnType = this.method.getReturnType();
			if (returnType.equals(String.class)) {
				return HttpResponse.ok((String)this.method.invoke(action, arguments.toArray(new Object[0])));
			}else if (Response.class.isAssignableFrom(returnType)) {
				return (Response)this.method.invoke(action, arguments.toArray(new Object[0]));
			}
			return null;
		} catch (IllegalAccessException e) {
			return HttpResponse.internalError(e.toString());
		} catch (IllegalArgumentException e) {
			return HttpResponse.internalError(e.toString());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return HttpResponse.internalError(e.toString());
		}
	}
	
	private static class Argument {
		private int index;
		private Class<?> type;
		
		public Argument(int index, Class<?> type) {
			this.index = index;
			this.type = type;
		}
	}
}