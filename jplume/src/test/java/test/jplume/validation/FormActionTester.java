package test.jplume.validation;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jplume.http.Response;
import jplume.view.View;
import jplume.view.ViewFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import test.jplume.JPlumeTester;

public class FormActionTester extends JPlumeTester {

	private View view = null;
	@Before
	public void setUp() throws Exception {
		super.setUp();
		Method method = FormAction.class.getMethod("submit", new Class<?>[]{String.class, FormBean.class});
		view = ViewFactory.createView(method);
	}

	@Test
	public void test() {
		Map<String, String> namedVars = Collections.emptyMap();
		Map<String, String> values = new HashMap<String, String>();
		values.put("id", "aa");
		values.put("name", "hello");
		values.put("age", "20");
		values.put("gender", "female");
		System.out.println(JSONObject.fromObject(values));
		Response t = view.handle(new TestRequest(values), new String[]{"ok"}, namedVars);
	}

}
