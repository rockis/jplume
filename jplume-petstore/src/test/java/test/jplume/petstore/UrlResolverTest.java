package test.jplume.petstore;

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
		Settings.initalize("petstore.json");
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
		
	}
}