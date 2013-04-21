package jplume.template.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.conf.Settings;
import jplume.core.Environ;
import jplume.http.HttpResponse;
import jplume.http.Response;
import jplume.template.BuiltinFunctions;
import jplume.template.TemplateEngine;
import jplume.template.annotations.TemplateFunctionObject;
import jplume.utils.ClassUtil;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;

public class FreemarkerEngine extends TemplateEngine {

	private Logger logger = LoggerFactory.getLogger(FreemarkerEngine.class);

	private Configuration config = null;

	public FreemarkerEngine() {
	}

	@Override
	public TemplateEngine initialize() {
		try {
			config = createConfiguration(Environ.getContext());
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
		BeansWrapper wrapper = new BeansWrapper();
       wrapper.setExposureLevel(BeansWrapper.EXPOSE_ALL);
      
       c.setObjectWrapper(wrapper);
       
       c.setSharedVariable("base", Environ.getContext().getContextPath());;
		c.setSharedVariable("settings", new StaticClassModel(Settings.class, wrapper));
		c.setSharedVariable("request", wrapper.wrap(new RequestModel()));
		
		Map<String, Object> engineConfig = Settings.getMap("TEMPLATE_ENGINES");
		@SuppressWarnings("unchecked")
		Map<String, String> properties = (Map<String, String>)engineConfig.get("freemarker");
		for(Map.Entry<String, String> entry : properties.entrySet()) {
			c.setSetting(entry.getKey(), entry.getValue());
		}
		createFunctions(engineConfig, c, wrapper);
		return c;
	}
	
	protected void createFunctions(Map<String, Object> engineConfig, Configuration config, BeansWrapper wrapper) {
		createFunctions(config, BuiltinFunctions.class, wrapper);
		List<String> functionNames = Settings.getList("TEMPLATE_FUNCTIONS");
		for (String funcClass : functionNames) {
			createFunctions(config, funcClass, wrapper);
		}
	}
	
	protected void createFunctions(Configuration config, String className, BeansWrapper wrapper) {
		try {
			Class<?> funcClass = ClassUtil.forName(className);
			createFunctions(config, funcClass, wrapper);
		} catch (ClassNotFoundException e) {
			logger.error(
					"Error while create builtin functions",
					e);
		}
	}
	
	protected void createFunctions(Configuration config, Class<?> funcClass, BeansWrapper wrapper) {
		try {
			TemplateFunctionObject anno = funcClass.getAnnotation(TemplateFunctionObject.class);
			Object obj = funcClass.newInstance();
			if (anno == null || anno.namespace().isEmpty()) {
				Map<String, List<Method>> funcs = new HashMap<>();
				for(Method m : funcClass.getMethods()) {
					if (!funcs.containsKey(m.getName())) {
						funcs.put(m.getName(), new ArrayList<Method>());
					}
					funcs.get(m.getName()).add(m);
				}
				for (Map.Entry<String, List<Method>> e : funcs.entrySet()) {
					config.setSharedVariable(e.getKey(), new FunctionWrapper(obj, e.getKey(), e.getValue().toArray(new Method[0]), wrapper));
				}
			}else{
				config.setSharedVariable(anno.namespace(), wrapper.wrap(obj));
			}
		} catch (InstantiationException | IllegalAccessException
				 | TemplateModelException e) {
			logger.error(
					"Error while create builtin functions",
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
