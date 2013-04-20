package jplume.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.core.Environ;
import jplume.http.HttpJsonResponse;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.validation.Validator;

public class View {

	private Logger logger = LoggerFactory.getLogger(View.class);

	private final Method method;
	
	/**
	 * key: index of pathvar
	 */
	private final ArgumentBuilder argBuilder;
	private final String[] requireMethods;

	public View(Method method, ArgumentBuilder argBuilder, 
			            String[] requireMethods) {
		this.method    = method;
		this.argBuilder = argBuilder;
		this.requireMethods = requireMethods;
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
			Object object = null;
			if ((method.getModifiers() & Modifier.STATIC) == 0) { // method is not static method
				object = Environ.createInstanceOf(this.method.getDeclaringClass());
			}
			Object[] args = argBuilder.build(request, indexedVars, namedVars);
			
			Response resp = validate(object, request, args);
			if (resp != null) {
				return resp;
			}
			
			Class<?> returnType = this.method.getReturnType();
			if (Response.class.isAssignableFrom(returnType)) {
				List<Object> arglist = new LinkedList<>(Arrays.asList(args));
				if (argBuilder.hasFormArg()) {
					arglist.add(null);
				}
				resp = (Response) this.method.invoke(object, arglist.toArray(new Object[0]));
			} else {
				Object retval = this.method.invoke(object, args);
				if (retval != null) {
					return HttpResponse.ok(retval.toString());
				}else{
					return HttpResponse.ok("Null");
				}
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
	
	protected Response validate(Object object, Request request, Object[] args) {
		String validateMethodName = "validate" + StringUtils.capitalize(method.getName());
		try {
			List<Class<?>> argumentTypes = new LinkedList<>(Arrays.asList(argBuilder.getArgumentTypes()));
			argumentTypes.add(Request.class);
			argumentTypes.add(Validator.class);
			Method validateMethod = method.getDeclaringClass().getMethod(validateMethodName, argumentTypes.toArray(new Class<?>[0]));
			if (validateMethod != null) {
				List<Object> arglist = new LinkedList<>(Arrays.asList(args));
				arglist.add(request);
				
				Validator validator = new Validator(request);
				arglist.add(validator);
				Response res = (Response)validateMethod.invoke(object, arglist.toArray(new Object[0]));
				if (res != null) {
					return res;
				}
				if (validator.hasError()) {
					return HttpJsonResponse.error(validator.getErrors(), validator.getFieldErrors());
				}
				return null;
			}
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException |InvocationTargetException e) {
			throw new RuntimeException(e);
		} 
	}

}