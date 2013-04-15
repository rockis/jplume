package jplume.template.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jplume.utils.ClassUtil;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;

public class MultiClassTemplateLoader implements TemplateLoader {

	private Map<String, TemplateLoader> loaders = Collections.synchronizedMap(new HashMap<String, TemplateLoader>());
	
	private String path;
	
	public MultiClassTemplateLoader(String path) {
		this.path = path;
	}
	
	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource != null) {
			((MultiSource)templateSource).close();
		}
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		int indexOfColon = name.lastIndexOf(':');
		if (indexOfColon > 0 ){
			String className = name.substring(0, indexOfColon);
			name = name.substring(indexOfColon + 1);
			TemplateLoader loader = loaders.get(className);
			if (loader == null) { 
				try {
					Class<?> clazz = ClassUtil.forName(className);
					loader = new ClassTemplateLoader(clazz, path);
					loaders.put(className, loader);
				} catch (ClassNotFoundException e) {
					throw new IOException("Class '" + className + "' not found");
				}
			}
			Object source = loader.findTemplateSource(name);
			if (source != null) {
				return new MultiSource(source, loader);
			}
		}
		return null;
	}

	@Override
	public long getLastModified(Object templateSource) {
		return ((MultiSource)templateSource).getLastModified();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		return ((MultiSource)templateSource).getReader(encoding);
	}
	
	private static final class MultiSource {
		private final Object source;
		private final TemplateLoader loader;

		MultiSource(Object source, TemplateLoader loader) {
			this.source = source;
			this.loader = loader;
		}

		long getLastModified() {
			return loader.getLastModified(source);
		}

		Reader getReader(String encoding) throws IOException {
			return loader.getReader(source, encoding);
		}

		void close() throws IOException {
			loader.closeTemplateSource(source);
		}

		public boolean equals(Object o) {
			if (o instanceof MultiSource) {
				MultiSource m = (MultiSource) o;
				return m.loader.equals(loader) && m.source.equals(source);
			}
			return false;
		}

		public int hashCode() {
			return loader.hashCode() + 31 * source.hashCode();
		}

		public String toString() {
			return source.toString();
		}
	}
}
