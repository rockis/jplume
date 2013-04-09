package jplume.template.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.TemplateLoader;

public class PrefixTemplateLoader implements TemplateLoader {

	private TemplateLoader defaultLoader = null;

	private Map<String, TemplateLoader> loaders = Collections.synchronizedMap(new HashMap<String, TemplateLoader>());
	
	public PrefixTemplateLoader(TemplateLoader defaultLoader) {
		this.defaultLoader = defaultLoader;
	}
	
	public void addLoader(String prefix, TemplateLoader loader) {
		this.loaders.put(prefix, loader);
	}
	
	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		 ((PrefixSource)templateSource).close();
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		int indexOfColon = name.indexOf(':');
		Object templateSource = null;
		if (indexOfColon > 0 ){
			String prefix = name.substring(0, indexOfColon);
			name = name.substring(indexOfColon + 1);
			TemplateLoader loader = loaders.get(prefix);
			if (loader == null) {
				throw new IOException("Prefix '" + prefix + "' doesn't exists");
			}
			templateSource = loader.findTemplateSource(name);
		}
		templateSource = defaultLoader.findTemplateSource(name);
		if (templateSource != null) {
			return new PrefixSource(defaultLoader.findTemplateSource(name), defaultLoader);
		}
		return null;
	}

	@Override
	public long getLastModified(Object templateSource) {
		return ((PrefixSource)templateSource).getLastModified();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		 return ((PrefixSource)templateSource).getReader(encoding);
	}

	private static final class PrefixSource {
		private final Object source;
		private final TemplateLoader loader;

		PrefixSource(Object source, TemplateLoader loader) {
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
			if (o instanceof PrefixSource) {
				PrefixSource m = (PrefixSource) o;
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
