package jplume.Interceptors;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.conf.Settings;
import jplume.http.Request;
import jplume.http.Response;
import jplume.view.ViewMethod;

public class CommonInterceptor extends InterceptorAdapter {

	private Map<Integer, ViewMethod> errorMethods = new HashMap<Integer, ViewMethod>();
	
	private Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);
	
	public CommonInterceptor() {
		
		Map<Integer, String> errorHandlers = Settings.getMap("ERROR_HANDLERS");
		for (Map.Entry<Integer, String> entry : errorHandlers.entrySet()) {
			ViewMethod m = getErrorView(entry.getValue());
			if (m != null) {
				errorMethods.put(entry.getKey(), m);
			}
		}
	}
	
	@Override
	public Response processResponse(Request request, Response response) {
		
		int code = response.getCode();
		if (errorMethods.containsKey(code)) {
			return errorMethods.get(code).handle(request);
		}
		return null;
	}

	public ViewMethod getErrorView(String viewName) {
		int lastIndexOfDot = viewName.lastIndexOf(".");
		String className = viewName.substring(0, lastIndexOfDot);
		String methodName = viewName.substring(lastIndexOfDot + 1);
		try {
			Class<?> clz = Class.forName(className);
			Method method = clz.getMethod(methodName, new Class[]{ Request.class });
			return ViewMethod.create(method);
		} catch (Exception e) {
			logger.warn("The error handler " + viewName + " not found or invalid", e);
			return null;
		} 
	}
}
