package jplume.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.converters.Converter;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.View;

public class ViewMethod {

	private Logger logger = LoggerFactory.getLogger(ViewMethod.class);

	private final Pattern pattern;
	private final Method method;
	
	/**
	 * key: index of pathvar
	 */
	private final ArgumentBuilder argBuilder;
	private final String[] requireMethods;

	private ViewMethod(Pattern pattern, Method method, ArgumentBuilder argBuilder, 
			            String[] requireMethods) {
		this.pattern   = pattern;
		this.method    = method;
		this.argBuilder = argBuilder;
		this.requireMethods = requireMethods;
	}
	
	public static ViewMethod create(Pattern pattern, Method method) throws ViewException {
		Class<?>[] argumentTypes = method.getParameterTypes();

		ArgumentBuilder argBuilder = new ArgumentBuilder();
		int argIndex = 0;
		for (Annotation[] annotations : method.getParameterAnnotations()) {
			if (annotations.length > 1) {
				throw new ViewException("View method's argument has one annotation as most");
			}else if(annotations.length == 1) {
				Annotation annotation = annotations[0];
				if (annotation instanceof PathVar) {
					PathVar anno = (PathVar) annotation;
					if (anno.name().length() == 0) {
						argBuilder.addArgument(new PathIndexedArgument(
							argumentTypes[argIndex], argIndex));
					}else{
						argBuilder.addArgument(new PathNamedArgument(
								argumentTypes[argIndex], anno.name()));
					}
				}else if (annotation instanceof QueryVar) {
					QueryVar anno = (QueryVar) annotation;
					String name = anno.name();
					String defval = anno.defval();
					argBuilder.addArgument(new QueryArgument(argumentTypes[argIndex], name, defval));
				}else {
					throw new ViewException("Annotation of view method's argument must be PathVar or QueryVar");
				}
			}else{
				throw new ViewException("Invalid Argument:" + argIndex + " of method " + method.getDeclaringClass() + ":" + method.getName());
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
	
	public Pattern getPattern() {
		return pattern;
	}

	public Method getMethod() {
		return method;
	}

	public String[] getRequireMethods() {
		return requireMethods;
	}

	public ArgumentBuilder getArgBuilder() {
		return argBuilder;
	}
	
	public Response handle(Request request, String[] indexedVars, Map<String, String> namedVars) {
		try {
			if (requireMethods.length > 0
					&& Arrays.binarySearch(requireMethods, request.getMethod().toLowerCase()) < 0) {
				return HttpResponse.forbidden();
			}
			Class<?> actionClass = this.method.getDeclaringClass();
			Object action = null;
			if ((method.getModifiers() & Modifier.STATIC) == 0) { // method is static method
				action = actionClass.newInstance();
			}
			
			Object[] args = argBuilder.build(request, indexedVars, namedVars);
			
			Response resp = null;
			Class<?> returnType = this.method.getReturnType();
			if (returnType.equals(String.class)) {
				resp = HttpResponse.ok((String) this.method.invoke(action, args));
			} else if (Response.class.isAssignableFrom(returnType)) {
				resp = (Response) this.method.invoke(action, args);
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

	public static abstract class Argument {
		
		protected Class<?> type;
		public Argument(Class<?> type) {
			this.type = type;
		}
		
		public Class<?> getType() {
			return type;
		}
		
		protected Object convert(String val) {
			return Converter.convert(this.type, val);
		}
	}
	
	public static abstract class PathArgument extends Argument {
		public PathArgument(Class<?> type) {
			super(type);
		}
		
		protected boolean validate(String val) {
			return Converter.validate(type, val);
		}
		
		protected abstract boolean validate(String[] indexedVars, Map<String, String> namedVars);
	}
	public static class PathIndexedArgument extends PathArgument{
		private int pathIndex;

		private PathIndexedArgument(Class<?> type, int pathIndex) {
			super(type);
			this.pathIndex = pathIndex;
		}
		
		public int getArgIndex() {
			return this.pathIndex;
		}

		protected Object get(String[] indexedVars) {
			return convert(indexedVars[pathIndex]);
		}
		
		protected boolean validate(String[] indexedVars, Map<String, String> namedVars) {
			return validate(indexedVars[pathIndex]);
		}
	}
	
	public static class PathNamedArgument extends PathArgument{
		private String argName = null;

		public PathNamedArgument(Class<?> type, String argName) {
			super(type);
			this.argName = argName;
		}

		public String getArgName() {
			return argName;
		}
		
		private Object get(Map<String, String> namedVars) {
			return convert(namedVars.get(this.argName));
		}
		
		protected boolean validate(String[] indexedVars, Map<String, String> namedVars) {
			String val = namedVars.get(argName);
			if (val == null) {
				return false;
			}
			return validate(namedVars.get(argName));
		}
	}
	
	
	public static class QueryArgument extends Argument {
		private String name;
		private String defval;

		private QueryArgument(Class<?> type, String name, String defval) {
			super(type);
			this.name   = name;
			this.defval = defval;
		}

		private Object get(Request request) {
			String val = request.getParam(name);
			if (val == null || !Converter.validate(this.type, val)) {
				return convert(defval);
			}
			return convert(val);
		}
	}
	
	public static class ArgumentBuilder {
		
		private List<PathIndexedArgument> pathIndexedArgs = new ArrayList<>();
		private List<PathNamedArgument> pathNamedArgs = new ArrayList<>();
		private List<QueryArgument> querydArgs = new ArrayList<>();
		
		private void addArgument(Argument arg){
			if (arg instanceof PathIndexedArgument){
				this.pathIndexedArgs.add((PathIndexedArgument)arg);
			}else if (arg instanceof PathNamedArgument) {
				this.pathNamedArgs.add((PathNamedArgument)arg);
			}else if (arg instanceof QueryArgument) {
				this.querydArgs.add((QueryArgument)arg);
			}else{
				throw new IllegalStateException("Unsupported argument type");
			}
		}
		
		public boolean validate(String[] indexedVars, Map<String, String> namedVars) {
			for(Argument arg : pathIndexedArgs) {
				if (!((PathArgument)arg).validate(indexedVars, namedVars)){
					return false;
				}
			}
			for(Argument arg : pathNamedArgs) {
				if (!((PathNamedArgument)arg).validate(indexedVars, namedVars)){
					return false;
				}
			}
			return true;
		}
		
		private Object[] build(Request request, String[] indexedVars, Map<String, String> namedVars) {
			Object[] args = new Object[pathIndexedArgs.size() + pathNamedArgs.size() + querydArgs.size() ];
			int i = 0;
			if (pathNamedArgs.size() == 0) {
				for (PathIndexedArgument arg : pathIndexedArgs) {
					args[i++] = arg.get(indexedVars);
				}
			}else{
				for (PathNamedArgument arg : pathNamedArgs) {
					args[i++] = arg.get(namedVars);
				}
			}
			for (QueryArgument arg : querydArgs) {
				args[i++] = arg.get(request);
			}
			return args;
		}

		public List<PathIndexedArgument> getPathIndexedArgs() {
			return Collections.unmodifiableList(pathIndexedArgs);
		}

		public List<PathNamedArgument> getPathNamedArgs() {
			return Collections.unmodifiableList(pathNamedArgs);
		}

		public List<QueryArgument> getQuerydArgs() {
			return Collections.unmodifiableList(querydArgs);
		}
		
		
	}
}