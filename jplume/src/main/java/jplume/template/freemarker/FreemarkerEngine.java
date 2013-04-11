package jplume.template.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.conf.Settings;
import jplume.core.ActionContext;
import jplume.http.HttpResponse;
import jplume.http.Response;
import jplume.template.TemplateEngine;
import jplume.template.annotations.TemplateFunction;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerEngine extends TemplateEngine {

	private Logger logger = LoggerFactory.getLogger(FreemarkerEngine.class);

	private Configuration config = null;

	public FreemarkerEngine() {
	}

	@Override
	public TemplateEngine initialize() {
		try {
			config = createConfiguration(ActionContext.getContext());
		} catch (TemplateException e) {
			logger.error(e.getMessage());
		}
		return this;
	}

	@Override
	public Response render(String template) {
		try {
			Template t = config.getTemplate(template);
			return new TemplateResponse(t, null);
		} catch (IOException e) {
			return HttpResponse.internalError("Template " + template + " not found", e);
		}
	}

	@Override
	public Response render(String template, Object data) {
		try {
			Template t = config.getTemplate(template, "utf-8");
			return new TemplateResponse(t, data);
		} catch (IOException e) {
			return HttpResponse.internalError("Template " + template + " not found", e);
		}
	}

	@Override
	public Response render(Class<?> clazz, String template) {
		template = "cl:" + clazz.getName() + ":" + template;
		return render(template);
	}

	@Override
	public Response render(Class<?> clazz, String template, Object context) {
		template = "cl:" + clazz.getName() + ":" + template;
		return render(template, context);
	}

	protected TemplateLoader getTemplateLoader(ServletContext servletContext) {
		TemplateLoader defaultLoader = null;
		if (servletContext == null) {
			try {
				defaultLoader = new FileTemplateLoader(new File("/tmp"));
			} catch (IOException e) {
			}
		}else{
			defaultLoader = new WebappTemplateLoader(servletContext);
		}
		PrefixTemplateLoader loader = new PrefixTemplateLoader(defaultLoader);
		loader.addLoader("cl", new MultiClassTemplateLoader("templates"));
		return loader;
	}

	protected Configuration createConfiguration(ServletContext servletContext)
			throws TemplateException {
		Configuration c = new Configuration();
		c.setLocalizedLookup(false);
		c.setTemplateLoader(getTemplateLoader(servletContext));
		
		c.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		
		Map<String, Object> engineConfig = Settings.getMap("TEMPLATE_ENGINE");
		createBuiltinFunctions(engineConfig, c);
		return c;
	}
	
	protected void createBuiltinFunctions(Map<String, Object> engineConfig, Configuration config) {
		
		@SuppressWarnings("unchecked")
		List<String> funcs = (List<String>)engineConfig.get("functions");
		for(String className : funcs) {
			createBuiltinFunctions(config, className);
		}
	}
	
	protected void createBuiltinFunctions(Configuration config, String className) {
		try {
			Class<?> funcClass = Class.forName(className);
			Object obj = funcClass.newInstance();
			for(Method m : funcClass.getMethods()) {
				TemplateFunction anno = m.getAnnotation(TemplateFunction.class);
				if (anno == null) continue;
				String name = m.getName();
				if (!anno.name().isEmpty()) {
					name = anno.name();
				}
				config.setSharedVariable(name, new FunctionWrapper(obj, m));
			}
			
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.error(
					"Error while create builtin function",
					e);
		}
		
	}

	protected void loadSettings(ServletContext servletContext,
			Configuration configuration) {
		InputStream in = null;

		try {
			in = getClass().getClassLoader().getResourceAsStream(
					"freemarker.properties");

			if (in != null) {
				Properties p = new Properties();
				p.load(in);
				configuration.setSettings(p);
			}
		} catch (IOException e) {
			logger.error(
					"Error while loading freemarker settings from /freemarker.properties",
					e);
		} catch (TemplateException e) {
			logger.error(
					"Error while loading freemarker settings from /freemarker.properties",
					e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException io) {
					logger.warn("Unable to close input stream", io);
				}
			}
		}
	}
}
