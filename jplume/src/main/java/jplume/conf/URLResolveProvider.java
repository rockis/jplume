package jplume.conf;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.core.RequestDispatcher;
import jplume.utils.ClassUtil;
import jplume.utils.ExceptionUtil;
import jplume.view.View;
import jplume.view.ViewFactory;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.ViewMethod;

public abstract class URLResolveProvider {
	
	private static Logger logger = LoggerFactory.getLogger("url");
	
	abstract public <T> T visit(String path, URLVisitor<T> visitor) throws IllegalURLPattern;
	
	abstract void addRegexPrefix(String prefix);
	
	public static URLResolverGroup create(String urlconf) {
		if (urlconf.endsWith(".urls")) { // urlconf is xxx.urls
			URL url = RequestDispatcher.class.getClassLoader().getResource(
					urlconf);
			if (url == null) {
				throw new IllegalURLPattern("Cannot read urlconf '"
						+ urlconf + "'");
			}
			// urlconf in classpath
			return create(url);
		} else { // urlconf is class name
			try {
				Class<?> actionClass = ClassUtil.forName(urlconf);
				return create(actionClass);
			} catch (ClassNotFoundException e) {
				throw new IllegalURLPattern("Cannot read urlconf '"
						+ urlconf + "'", e);
			}
		}
	}

	public static URLResolverGroup create(URL urlconf) {
		assert urlconf != null;

		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine jsEngine = sem.getEngineByName("js");
		try {
			jsEngine.eval("importPackage(Packages.jplume.conf);");
			jsEngine.eval("patterns = URLResolveProvider.patterns;");
			jsEngine.eval("_ = pattern = URLResolveProvider.pattern;");
			jsEngine.eval("include = URLResolveProvider.include;");

			jsEngine.eval(new InputStreamReader(urlconf.openStream()));
			return (URLResolverGroup) jsEngine.get("urlpatterns");

		} catch (ScriptException e) {
			try {
				ExceptionUtil.logScriptException(logger, e, urlconf);
			} catch (IOException e1) {
			}
			throw new IllegalURLPattern("Urlconf '"
					+ urlconf.toString() + "' has an error in line " + e.getLineNumber(), e);
			
		} catch (IOException e) {
			throw new IllegalURLPattern("Cannot read urlconf '"
					+ urlconf.toString() + "'", ExceptionUtil.last(e));
		}
	}

	public static URLResolverGroup create(Class<?> actionClass) {
		Prefix prefix = actionClass.getAnnotation(Prefix.class);
		
		Method[] views = actionClass.getMethods();
		List<URLResolver> patterns = new ArrayList<URLResolver>();
		for (Method view : views) {
			ViewMethod anno = view.getAnnotation(ViewMethod.class);
			if (anno != null && !anno.regex().isEmpty()) {
				URLResolver ur = new URLResolver(anno.regex(), ViewFactory.createView(view));
				if (prefix != null) {
					ur.addRegexPrefix(prefix.regex());
				}
				patterns.add(ur);
			}
		}
		return new URLResolverGroup(patterns.toArray(new URLResolver[0]));
	}
	
	
	public static URLResolverGroup patterns(URLResolveProvider... patterns) {
			return new URLResolverGroup(patterns);
	}

	
	public static URLResolveProvider pattern(String regex, String classMethodName) {
		try {
			View view = ViewFactory.createView(classMethodName);
			return new URLResolver(regex, view);
		} catch (Exception e) {
			throw new IllegalURLPattern(e.getMessage());
		}
		
	}

	/**
	 * 
	 * @param regex
	 * @param patterns
	 * @return
	 */
	public static URLResolveProvider pattern(String regex, URLResolveProvider patterns) {
		patterns.addRegexPrefix(regex);
		return patterns;
	}

	/**
	 * eg. _("^/include", include("test/jplume/urlresolver/include.urls"))
	 * @param urlconf
	 * @return
	 */
	public static URLResolverGroup include(String urlconf) {
		return create(urlconf);
	}
}