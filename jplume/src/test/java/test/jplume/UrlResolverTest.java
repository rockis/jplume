package test.jplume;

import java.net.URL;
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
		URL u = getClass().getClassLoader().getResource("test/jplume/urlresolver/test.urls");
		URLResolveProvider urp = URLResolveProvider.create("test/jplume/urlresolver/test.urls");
		// test _("^$", "index"),
		ViewMethod result = urp.visit("", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals(pattern.toString(), "^$");
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "index");
		
		// test _("^/help$", "help"),
		result = urp.visit("/help", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars,
					ViewMethod method, boolean matched) {
				if (matched) {
					assertEquals(pattern.toString(), "^/help$");
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "help");
		
		// _("^/helloworld$", "test.jplume.urlresolver.TestAction2.helloworld"),
		result = urp.visit("/helloworld", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars,
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
			public ViewMethod visit(Pattern pattern, String[] pathVars,
					ViewMethod method, boolean matched) { 
				if (matched) {
					assertEquals(pattern.toString(), "^/include$");
					return method;
				}
				return null;
			}
		});
		assertEquals(result.getMethod().getName(), "include");
		
		// test _("^/include/param/(\d+)/([\w]+)/(\d+)$", "test.jplume.urlresolver.TestAction3.include"),
		result = urp.visit("/include/param/19/name/20", new URLVisitor<ViewMethod>() {
			@Override
			public ViewMethod visit(Pattern pattern, String[] pathVars,
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
		URL u = getClass().getClassLoader().getResource("test/jplume/urlresolver/test.urls");
		URLResolveProvider urp = URLResolveProvider.create("test/jplume/urlresolver/test.urls");
		URLReverser ur = new URLReverser(urp);
		System.out.println(ur.reverse("test.jplume.urlresolver.TestAction3", "param", new String[]{"22", "name", "20"}));
	}
}
