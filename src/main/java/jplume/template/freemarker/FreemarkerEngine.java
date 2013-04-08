package jplume.template.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.core.ActionContext;
import jplume.http.HttpResponse;
import jplume.http.Response;
import jplume.template.TemplateEngine;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
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
	public void initialize() {
		try {
			config = createConfiguration(ActionContext.getContext());
		} catch (TemplateException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public Response render(String template) {
		try {
			Template t = config.getTemplate(template);
			return new TemplateResponse(t, null);
		} catch (IOException e) {
			return HttpResponse.notFound();
		}
	}

	@Override
	public Response render(String template, Object context) {
		try {
			Template t = config.getTemplate(template, "utf-8");
			return new TemplateResponse(t, context);
		} catch (IOException e) {
			return HttpResponse.notFound();
		}
	}

	protected TemplateLoader getTemplateLoader(ServletContext servletContext) {
		// construct a FileTemplateLoader for the init-param 'TemplatePath'
		FileTemplateLoader templatePathLoader = null;

		String templatePath = servletContext.getInitParameter("TemplatePath");
		if (templatePath == null) {
			templatePath = servletContext.getInitParameter("templatePath");
		}

		if (templatePath != null) {
			try {
				templatePathLoader = new FileTemplateLoader(new File(
						templatePath));
			} catch (IOException e) {
				logger.error(
						"Invalid template path specified: " + e.getMessage(), e);
			}
		}

		if (templatePathLoader != null){
			return new MultiTemplateLoader(
				new TemplateLoader[] { templatePathLoader, new WebappTemplateLoader(servletContext) 
			});
		} else{
			return new MultiTemplateLoader(
						new TemplateLoader[] {
								new WebappTemplateLoader(servletContext) 
						});
		}
	}

	protected Configuration createConfiguration(ServletContext servletContext)
			throws TemplateException {
		Configuration configuration = new Configuration();

		configuration.setTemplateLoader(getTemplateLoader(servletContext));

		configuration
				.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

		// configuration.setObjectWrapper(getObjectWrapper());

		// if( mruMaxStrongSize > 0 ) {
		// configuration.setSetting(freemarker.template.Configuration.CACHE_STORAGE_KEY,
		// "strong:" + mruMaxStrongSize);
		// }
		//
		// if (encoding != null) {
		// configuration.setDefaultEncoding(encoding);
		// }

		loadSettings(servletContext, configuration);

		return configuration;
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
