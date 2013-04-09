package jplume.template;

import jplume.conf.Settings;
import jplume.http.Response;

public abstract class TemplateEngine {


	private static TemplateEngine engine = null;
	private static Object lock = new Object();
	
	public static TemplateEngine get() {
		synchronized (lock) {
			if (engine == null) {
				try {
					@SuppressWarnings("unchecked")
					Class<TemplateEngine> engineClass = (Class<TemplateEngine>)Class.forName(Settings.templateEngineClass());
					engine = engineClass.newInstance();
					engine.initialize();
				} catch (ClassNotFoundException e) {
					throw new TemplateException("Template engine not found", e);
				} catch (IllegalAccessException e) {
					throw new TemplateException("Could not create template engine", e);
				} catch (InstantiationException e) {
					throw new TemplateException("Could not create template engine", e);
				}
			}
		}
		return engine;
	}
	
	public abstract void initialize();
	
	public abstract Response render(String template);
	
	public abstract Response render(String template, Object context);
	
	public abstract Response render(Class<?> clazz, String template);
	
	public abstract Response render(Class<?> clazz, String template, Object context);
	
}
