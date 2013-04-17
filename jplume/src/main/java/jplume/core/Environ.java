package jplume.core;

import javax.servlet.ServletContext;

import jplume.conf.ObjectFactory;
import jplume.http.Request;
import jplume.view.View;

public class Environ {

	private static ServletContext context = null;
	
	private static ObjectFactory actionFactory = null;
	
	private static ThreadLocal<Request> request = new ThreadLocal<>();
	
	private static ThreadLocal<View> view = new ThreadLocal<>();
	
	public static void setContext(ServletContext cntx) {
		assert context == null;
		context = cntx;
	}

	public static ServletContext getContext() {
		return context;
	}
	
	public static void setActionFactory(ObjectFactory factory) {
		actionFactory = factory;
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
}
