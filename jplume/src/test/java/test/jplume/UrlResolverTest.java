package test.jplume;

import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

import jplume.conf.URLResolveProvider;
import jplume.conf.URLReverser;
import jplume.conf.URLVisitor;
import jplume.view.ViewMethod;

import org.junit.Test;
import static org.junit.Assert.*;

public class UrlResolverTest {

	@Test
	public void test() {
		URLResolveProvider urp = URLResolveProvider.create("test/jplume/urlresolver/test.urls");
		// test _("^$", "index"),
		ViewMethod result = urp.visit("", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals(pattern.toString(), "^$");
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "index");
		
//	   test	_("^/indexed/([\\w]+)/([\\d]+)$", "indexedVars"),
		result = urp.visit("/indexed/plume/123", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals("plume", pathVars[0]);
					assertEquals("123", pathVars[1]);
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "indexedVars");
		
//	    test _("^/named/([\\w]+)/([\\d]+)$", "namedVars"),
		result = urp.visit("/named/plume/123", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals("plume", namedVars.get("arg1"));
					assertEquals("123", namedVars.get("arg2"));
					assertEquals(pathVars.length, 0);
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "namedVars");
		
//	    test _("^/indexed_named/([\\w]+)/([\\d]+)$", "indexedNamedVars"),
		result = urp.visit("/indexed_named/plume/123", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals("plume", namedVars.get("arg1"));
					assertEquals("123", namedVars.get("arg2"));
					assertEquals(pathVars.length, 0);
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "indexedNamedVars");
		
		
		// _("^/helloworld$", "test.jplume.urlresolver.TestAction2.helloworld"),
		result = urp.visit("/helloworld", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals(pattern.toString(), "^/helloworld$");
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "helloworld");
		
		// _("^/include$", "test.jplume.urlresolver.TestAction3.include"),
		result = urp.visit("/include", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) { 
				if (matched) {
					assertEquals(pattern.toString(), "^/include$");
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "includeme");
		// test _("^/include/param/(\d+)/([\w]+)/(\d+)$", "test.jplume.urlresolver.TestAction3.include"),
		result = urp.visit("/include/param/19/name/20", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals(pathVars.length, 3);
					assertEquals(pattern.toString(), "^/include/param/(\\d+)/([\\w]+)/(\\d+)$");
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "param");
	}

	@Test
	public void testReverse() {
		URLResolveProvider urp = URLResolveProvider.create("test/jplume/urlresolver/test.urls");
		URLReverser ur = new URLReverser(urp);
		String url = (ur.reverse("test.jplume.urlresolver.TestIncludeAction", "param", new String[]{"22", "name", "20"}));
		assertEquals("/include/param/22/name/20", url);
	}
}
