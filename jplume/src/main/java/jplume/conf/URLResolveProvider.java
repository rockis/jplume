package jplume.conf;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jplume.core.RequestDispatcher;
import jplume.view.annotations.View;

public abstract class URLResolveProvider {
	
	abstract public <T> T visit(String path, URLVisitor<T> visitor) throws URLResolveException;
	
	abstract void setRegexPrefix(String prefix);
	
	public static URLResolveProvider create(String urlconf) {
		if (urlconf.endsWith(".urls")) { // urlconf is xxx.urls
			URL url = RequestDispatcher.class.getClassLoader().getResource(
					urlconf);
			if (url == null) {
				throw new URLResolveException("Cannot read urlconf '"
						+ urlconf + "'");
			}
			// urlconf in classpath
			return create(url);
		} else { // urlconf is class name
			try {
				Class<?> actionClass = Class.forName(urlconf);
				return create(actionClass);
			} catch (ClassNotFoundException e) {
				throw new URLResolveException("Cannot read urlconf '"
						+ urlconf + "'", e);
			}
		}
	}

	public static URLResolveProvider create(URL urlconf) {
		assert urlconf != null;

		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine jsEngine = sem.getEngineByName("js");
		try {
			jsEngine.eval("importPackage(Packages.jplume.conf);");
			jsEngine.eval("patterns = URLResolveProvider.patterns;");
			jsEngine.eval("_ = pattern = URLResolveProvider.pattern;");
			jsEngine.eval("include = URLResolveProvider.include;");

			jsEngine.eval(new InputStreamReader(urlconf.openStream()));
			return (URLResolveProvider) jsEngine.get("urlpatterns");

		} catch (ScriptException e) {
			e.printStackTrace();
			throw new URLResolveException("Urlconf '"
					+ urlconf.toString() + "' has an error", e);
		} catch (IOException e) {
			throw new URLResolveException("Cannot read urlconf '"
					+ urlconf.toString() + "'", e);
		}
	}

	public static URLResolveProvider create(Class<?> actionClass) {
		Method[] views = actionClass.getMethods();
		List<URLResolver> patterns = new ArrayList<URLResolver>();
		for (Method view : views) {
			View anno = view.getAnnotation(View.class);
			if (anno != null && !anno.pattern().isEmpty()) {
				patterns.add(new URLResolver(view));
			}
		}
		return new URLResolverGroup(patterns.toArray(new URLResolver[0]));
	}
	
	
	public static URLResolveProvider patterns(String actionClassName,
			URLResolveProvider... patterns) {
		Class<?> actionClass;
		try {
			if (actionClassName.length() > 0) {
				actionClass = Class.forName(actionClassName);
				return new URLResolverGroup(actionClass, patterns);
			} else {
				return new URLResolverGroup(patterns);
			}
		} catch (ClassNotFoundException e) {
			throw new URLResolveException("Invalid Pattern: No Such Class '"
					+ actionClassName + "'");
		}
	}

	public static URLResolveProvider pattern(Pattern regex, String callback) {
		return new URLResolver(regex.toString(), callback);
	}
	
	public static URLResolveProvider pattern(String regex, String callback) {
		return new URLResolver(regex, callback);
	}

	public static URLResolveProvider pattern(String regex, URLResolveProvider patterns) {
		patterns.setRegexPrefix(regex);
		return patterns;
	}

	public static URLResolveProvider include(String urlconf) {
		return create(urlconf);
	}
}