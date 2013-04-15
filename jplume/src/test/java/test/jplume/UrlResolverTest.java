package test.jplume;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jplume.conf.Settings;
import jplume.conf.URLReverseException;
import jplume.conf.URLResolveProvider;
import jplume.conf.URLReverser;
import jplume.conf.URLVisitor;
import jplume.view.ViewMethod;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UrlResolverTest {

	URLResolveProvider urp = null;

	@Before
	public void setUp() throws Exception {
		Settings.initalize("jplume-test.json");
		urp =  URLResolveProvider.create(Settings.get("ROOT_URLCONF"));
		
//		URLResolveProvider provider = URLResolveProvider.create(Settings.get("ROOT_URLCONF"));
//		provider.visit("", new URLVisitor<String>() {
//			public String visit(Pattern pattern, String[] indexedVars, Map<String, String> namedVars, ViewMethod method, boolean matched) {
//				System.out.println(pattern.toString());
//				return null;
//			}
//		});
	}

	
	@Test
	public void test() {
		
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
		// test _("^/include/param/(\d+)/([\w]+)/([\d]+)$", "test.jplume.urlresolver.TestAction3.include"),
		result = urp.visit("/include/param/19/name/20", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals(pathVars.length, 3);
					assertEquals(pathVars[0], "19");
					assertEquals(pathVars[1], "name");
					assertEquals(pathVars[2], "20");
					assertEquals(pattern.toString(), "^/include/param/(\\d+)/([\\w]+)/([\\d]+)$");
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "param");
		
		// test _("^/p", "test.jplume.urlresolver.TestClassPrefixAction")
		result = urp.visit("/p/test/prefix", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "prefix");
		
		// test include(".TestClassIncludeAction")
		result = urp.visit("/includeclass/prefix", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "prefix2");
		
	}

	//@Test
	public void testReverse() {
		URLResolveProvider urp = URLResolveProvider.create("test/jplume/urlresolver/test.urls");
		URLReverser ur = new URLReverser(urp);
		String url = (ur.reverse(".TestIncludeAction", "param", new String[]{"22", "name", "20"}));
		assertEquals("/include/param/22/name/20", url);
		
		url = (ur.reverse(".TestSimpleAction", "indexedVars", new String[]{"nma", "20"}));
		assertEquals("/indexed/nma/20", url);
		
		Map<String, String> args = new HashMap<String, String>();
		args.put("arg1", "nma");
		args.put("arg2", "20");
		url = ur.reverse(".TestSimpleAction", "namedVars", args);
		assertEquals("/named/nma/20", url);
		args.put("arg3", "err");
		try {
			url = ur.reverse(".TestSimpleAction", "namedVars", args);
			assertTrue(false);
		} catch (URLReverseException e) {
			assertTrue(true);
		}
		args.clear();
		args.put("arg3", "err");
		try {
			url = ur.reverse(".TestSimpleAction", "namedVars", args);
			assertTrue(false);
		} catch (URLReverseException e) {
			assertTrue(true);
		}
	}
}
