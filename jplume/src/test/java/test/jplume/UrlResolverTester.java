package test.jplume;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import jplume.conf.URLResolveProvider;
import jplume.conf.URLReverseNotMatch;
import jplume.conf.URLReverser;
import jplume.conf.URLVisitor;
import jplume.view.View;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

interface Visitor {
	public void visit(String[] pathVars, Map<String, String> namedVars);
}
public class UrlResolverTester extends JPlumeTester {


	@Before
	public void setUp() throws Exception {
		
//		URLResolveProvider provider = URLResolveProvider.create(Settings.get("ROOT_URLCONF"));
//		provider.visit("", new URLVisitor<String>() {
//			public String visit(Pattern pattern, String[] indexedVars, Map<String, String> namedVars, ViewMethod method, boolean matched) {
//				System.out.println(pattern.toString());
//				return null;
//			}
//		});
	}

	private void test(String path, String methodName, final Visitor visitor) {
		View result = urp.visit(path, new URLVisitor<View>() {
			@Override
			public View visit(Pattern p, String[] pathVars, Map<String, String> namedVars,
					View method, boolean matched) {
				if (matched) {
					visitor.visit(pathVars, namedVars);
					return method;
				}
				return null;
			}
		});
		if (result == null) {
			fail(String.format("Not Match - path:%s, methodname:%s", path, methodName));
		}
		assertEquals(result.getMethod().getName(), methodName);
	}
	
	@Test
	public void test() {
		
		// test _("^$", "index")
		test("", "index", new Visitor() {
			@Override
			public void visit(String[] pathVars, Map<String, String> namedVars) {
				
			}
		});
		
//	   test	_("^/indexed/([\\w]+)/([\\d]+)$", "indexedVars"),
		test("/indexed/plume/123", "indexedVars", new Visitor() {
			@Override
			public void visit(String[] pathVars, Map<String, String> namedVars) {
				assertEquals("plume", pathVars[0]);
				assertEquals("123", pathVars[1]);
			}
		});
		
//	    test _("^/named/(?<arg1>[\\w]+)/(?<arg2>[\\d]+)$", "namedVars"),
		test("/named/plume/123", "namedVars", new Visitor() {
			@Override
			public void visit(String[] pathVars, Map<String, String> namedVars) {
				assertEquals("plume", namedVars.get("arg1"));
				assertEquals("123", namedVars.get("arg2"));
				assertEquals(pathVars.length, 0);
			}
		});
		
		//test "^/indexed_named/(?<arg1>[\\w]+)/(?<arg2>[\\d]+)$" indexedNamedVars
		test("/indexed_named/plume/123", "indexedNamedVars", new Visitor() {
			@Override
			public void visit(String[] pathVars, Map<String, String> namedVars) {
				assertEquals("plume", namedVars.get("arg1"));
				assertEquals("123", namedVars.get("arg2"));
				assertEquals(pathVars.length, 0);
			}
		});
		
		// test "^/helloworld$" helloworld
		test("/helloworld", "helloworld", new Visitor() {
			@Override
			public void visit(String[] pathVars, Map<String, String> namedVars) {
				
			}
		});
		
		// test ^/include$ includeme
		test("/include", "includeme", new Visitor() {
			@Override
			public void visit(String[] pathVars, Map<String, String> namedVars) {
				
			}
		});
		
		// test ^/p/<class TestClassPrefixAction(regex=test):prefix(regex=prefix)
		test("/include/param/19/name/20", "param", new Visitor() {
			@Override
			public void visit(String[] pathVars, Map<String, String> namedVars) {
				
			}
		});
		
		// test ^/p/<class TestClassPrefixAction(regex=test):prefix(regex=prefix)
		test("/includeclass/prefix", "prefix2", new Visitor() {
			@Override
			public void visit(String[] pathVars, Map<String, String> namedVars) {
				
			}
		});
		
	}

	@Test
	public void testReverse() {
		URLResolveProvider urp = URLResolveProvider.create("test/jplume/urlresolver/test.urls");
		URLReverser ur = new URLReverser(urp);
		String url = (ur.reverse(".TestIncludeAction", "param", new String[]{"22", "name", "20"}));
		assertEquals("/include/param/22/name/20", url);
		
		url = (ur.reverse(".TestSimpleAction", "indexedVars", new String[]{"nma", "20"}));
		assertEquals("/indexed/nma/20", url);
		
		url = (ur.reverse(".TestClassPrefixAction", "overload"));
		assertEquals("/p/test/overload", url);
		
		url = (ur.reverse(".TestClassPrefixAction", "overload", new String[]{"ooo"}));
		assertEquals("/p/test/overload/ooo", url);
		
		try {
			url = (ur.reverse(".TestClassPrefixAction", "overload", new String[]{"ooo", "fff"}));
			assertEquals("/p/test/overload/ooo", url);
			fail();
		} catch (Exception e1) {
		}
		
		Map<String, String> args = new HashMap<String, String>();
		args.put("arg1", "nma");
		args.put("arg2", "20");
		url = ur.reverse(".TestSimpleAction", "namedVars", args);
		assertEquals("/named/nma/20", url);
		args.put("arg3", "err");
		try {
			url = ur.reverse(".TestSimpleAction", "namedVars", args);
			fail();
		} catch (URLReverseNotMatch e) {
		}
		args.clear();
		args.put("arg3", "err");
		try {
			url = ur.reverse(".TestSimpleAction", "namedVars", args);
			fail();
		} catch (URLReverseNotMatch e) {
		}
	}
}
