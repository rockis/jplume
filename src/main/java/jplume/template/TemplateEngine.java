package jplume.template;

import java.util.HashMap;
import java.util.Map;

import jplume.conf.Settings;
import jplume.http.Response;
import jplume.template.freemarker.FreemarkerEngine;

public abstract class TemplateEngine {


	public static final String FREEMARKER = "freemarker";
	
	private static String defaultEngine = null;
	
	private static Map<String, TemplateEngine> engines = new HashMap<String, TemplateEngine>();
	
	private static Object lock = new Object();
	
	static {
		engines.put(FREEMARKER, new FreemarkerEngine().initialize());
	}
	
	public static TemplateEngine get() {
		synchronized (lock) {
			if (defaultEngine == null) {
				Map<String, Object> m = Settings.getMap("TEMPLATE_ENGINE");
				defaultEngine = (String)m.get("default");
			}
		}
		return get(defaultEngine);
	}
	
	public static TemplateEngine get(String engineName) {
		return engines.get(engineName);
	}
	
	public abstract TemplateEngine initialize();
	
	public abstract Response render(String template);
	
	public abstract Response render(String template, Object context);
	
	public abstract Response render(Class<?> clazz, String template);
	
	public abstract Response render(Class<?> clazz, String template, Object context);
	
}
