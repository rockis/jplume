package jplume.core;

import java.util.Map;

import javax.servlet.ServletContext;

import jplume.conf.ObjectFactory;
import jplume.conf.URLReverser;
import jplume.http.Request;
import jplume.view.View;

public class Environ {

	private static ServletContext context = null;
	
	private static ObjectFactory actionFactory = null;
	
	private static URLReverser urlReverser = null;
	
	private static ThreadLocal<Request> request = new ThreadLocal<>();
	
	private static ThreadLocal<View> view = new ThreadLocal<>();
	
	public static void setContext(ServletContext cntx) {
		assert context == null;
		Environ.context = cntx;
	}

	public static ServletContext getContext() {
		return context;
	}
	
	public static void setActionFactory(ObjectFactory factory) {
		Environ.actionFactory = factory;
	}
	
	public static void setUrlReverser(URLReverser urlReverser) {
		Environ.urlReverser = urlReverser;
	}

	public static String getContextPath() {
		return getContext().getContextPath();
	}
	
	public static String getRealPath(String resource) {
		return getContext().getRealPath(resource);
	}
	
	public static void setRequest(Request req) {
		assert request == null;
		request.set(req);
	}
	
	public static Request getRequest() {
		return request.get();
	}
	
	public static void setView(View v) {
		assert view == null;
		view.set(v);
	}
	
	public static View getView() {
		return view.get();
	}
	
	public static <T> T createInstanceOf(Class<T> type) {
		return actionFactory.createObject(type);
	}
	
	public static String reverseURL(Class<?> clazz, String methodName) {
		return Environ.urlReverser.reverse(clazz.getName(), methodName);
	}
	
	public static String reverseURL(Class<?> clazz, String methodName, String[] pathVars) {
		return Environ.urlReverser.reverse(clazz.getName(), methodName, pathVars);
	}
	
	public static String reverseURL(Class<?> clazz, String methodName, Map<String, String> namedVars) {
		return Environ.urlReverser.reverse(clazz.getName(), methodName, namedVars);
	}
}
